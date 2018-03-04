package mbd.s3ackup.daemon.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DesktopOpen {

	private static final Logger log = LoggerFactory.getLogger(DesktopOpen.class);

	public static void openBrower(URI oURL) {
		try {
			System.setProperty("java.awt.headless", "false");
			Desktop desktop = java.awt.Desktop.getDesktop();
			desktop.browse(oURL);
		}
		catch (Exception e) {
			log.error("Unable to open [" + oURL + "]", e);
		}
	}

	public static void openFile(Path path) {
		try {
			Desktop desktop = java.awt.Desktop.getDesktop();
			desktop.open(path.toFile());
		}
		catch (IOException e) {
			log.error("Unable to open [" + path + "]", e);
		}
	}

}