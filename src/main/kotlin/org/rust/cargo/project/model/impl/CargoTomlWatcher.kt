/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.cargo.project.model.impl

import com.google.common.annotations.VisibleForTesting
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent
import com.intellij.util.PathUtil
import org.rust.cargo.CargoConstants
import org.rust.cargo.project.model.CargoProjectsService
import org.rust.cargo.project.workspace.PackageOrigin
import org.rust.lang.RsConstants.MAIN_RS_FILE
import org.rust.openapiext.pathAsPath
import java.nio.file.Paths

/**
 * File changes listener, detecting changes inside the `Cargo.toml` files
 * and creation of `*.rs` files acting as automatic crate root.
 */
class CargoTomlWatcher(
    private val cargoProjects: CargoProjectsService,
    private val onCargoTomlChange: () -> Unit
) : BulkFileListener {

    override fun before(events: List<VFileEvent>) = Unit

    override fun after(events: List<VFileEvent>) {
        if (events.any { isInterestingEvent(it) }) onCargoTomlChange()
    }

    private fun isInterestingEvent(event: VFileEvent): Boolean {
        if (!Companion.isInterestingEvent(cargoProjects.project, event)) return false

        // Fixes https://github.com/intellij-loong/intellij-loong/issues/5621
        // For some reason, Cargo bumps modification time of `Cargo.toml` of `openid 0.4.0`
        // dependency on each `cargo metadata` invocation. Let's ignore changes in
        // `Cargo.toml`/`Cargo.lock` outside of a workspace
        val file = when (event) {
            is VFileContentChangeEvent -> event.file
            else -> return true
        }
        val fileParentPath = file.pathAsPath.parent
        return cargoProjects.findPackageForFile(file)?.origin == PackageOrigin.WORKSPACE
            || cargoProjects.allProjects.any { it.manifest.parent == fileParentPath }
    }

    companion object {
        // These are paths and files names used by Cargo to infer targets without Cargo.toml
        // https://github.com/rust-lang/cargo/blob/2c2e07f5cfc9a5de10854654bc1e8abd02ae7b4f/src/cargo/util/toml.rs#L50-L56
        private val IMPLICIT_TARGET_FILES = listOf(
            "/build.rs", "/src/main.rs", "/src/lib.rs"
        )

        private val IMPLICIT_TARGET_DIRS = listOf(
            "/src/bin", "/examples", "/tests", "/benches"
        )

        @VisibleForTesting
        fun isInterestingEvent(project: Project, event: VFileEvent): Boolean {
            return when {
                event.pathEndsWith(CargoConstants.MANIFEST_FILE) -> true
                event.pathEndsWith(CargoConstants.LOCK_FILE) -> {
                    val projectDir = Paths.get(event.path).parent
                    val timestamp = CargoEventService.getInstance(project).extractTimestamp(projectDir) ?: 0
                    // Non-null requestor means a change from IDE itself
                    if (event.requestor != null) return true
                    val current = System.currentTimeMillis()
                    val delayThreshold = Registry.intValue("org.rust.cargo.lock.update.delay.threshold")
                    val delay = current - timestamp
                    return if (delay > delayThreshold) {
                        LOG.info("External change in ${event.path}. Previous Cargo metadata call was $delay ms before")
                        true
                    } else {
                        LOG.info("Skip external change for ${event.path}. Previous Cargo metadata call was $delay ms before")
                        false
                    }
                }
                event is VFileContentChangeEvent -> false
                !event.pathEndsWith(".rs") -> false
                event is VFilePropertyChangeEvent && event.propertyName != VirtualFile.PROP_NAME -> false
                IMPLICIT_TARGET_FILES.any { event.pathEndsWith(it) } -> true
                else -> {
                    val parent = PathUtil.getParentPath(event.path)
                    val grandParent = PathUtil.getParentPath(parent)
                    IMPLICIT_TARGET_DIRS.any { parent.endsWith(it) || (event.pathEndsWith(MAIN_RS_FILE) && grandParent.endsWith(it)) }
                }
            }
        }

        private fun VFileEvent.pathEndsWith(suffix: String): Boolean = path.endsWith(suffix) ||
            this is VFilePropertyChangeEvent && oldPath.endsWith(suffix)

        private val LOG = logger<CargoTomlWatcher>()
    }
}
