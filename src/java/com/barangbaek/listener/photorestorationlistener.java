package com.barangbaek.listener;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/* Permanent storage:
 *   {user.home}/BarangBaekUploads/itemphoto
 *   {user.home}/BarangBaekUploads/userphoto
 *
 * Web paths:
 *   /assets/img/itemphoto
 *   /assets/img/userphoto
 */
public class photorestorationlistener
        implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();

        restoreDirectory(
                context,
                "itemphoto",
                "/assets/img/itemphoto"
        );

        restoreDirectory(
                context,
                "userphoto",
                "/assets/img/userphoto"
        );
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    private void restoreDirectory(
            ServletContext context,
            String permanentSubfolder,
            String deployedWebPath
    ) {
        String deployedPath = context.getRealPath(deployedWebPath);

        if (deployedPath == null) {
            context.log(
                    "Photo restoration skipped because the deployed "
                    + "directory could not be resolved: "
                    + deployedWebPath
            );
            return;
        }

        Path permanentDirectory = Paths.get(
                System.getProperty("user.home"),
                "BarangBaekUploads",
                permanentSubfolder
        );

        if (!Files.isDirectory(permanentDirectory)) {
            context.log(
                    "No permanent photo directory found yet: "
                    + permanentDirectory.toString()
            );
            return;
        }

        Path deployedDirectory = Paths.get(deployedPath);
        int restoredCount = 0;

        try {
            Files.createDirectories(deployedDirectory);

            try (DirectoryStream<Path> photos
                    = Files.newDirectoryStream(permanentDirectory)) {

                for (Path photo : photos) {
                    if (!Files.isRegularFile(photo)
                            || !isSupportedImage(photo)) {
                        continue;
                    }

                    Path target = deployedDirectory.resolve(
                            photo.getFileName().toString()
                    );

                    Files.copy(
                            photo,
                            target,
                            StandardCopyOption.REPLACE_EXISTING
                    );

                    restoredCount++;
                }
            }

            context.log(
                    "BarangBaek restored "
                    + restoredCount
                    + " photo(s) to "
                    + deployedWebPath
            );

        } catch (IOException e) {
            context.log(
                    "Unable to restore BarangBaek photos from "
                    + permanentDirectory.toString(),
                    e
            );
        }
    }

    private boolean isSupportedImage(Path photo) {
        String fileName = photo.getFileName()
                .toString()
                .toLowerCase(Locale.ENGLISH);

        return fileName.endsWith(".jpg")
                || fileName.endsWith(".jpeg")
                || fileName.endsWith(".png")
                || fileName.endsWith(".webp");
    }
}
