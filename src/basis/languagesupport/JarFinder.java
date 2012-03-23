package basis.languagesupport;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JarFinder {

	public static URL[] languageSupportJars(File root) {
		return findJars(root, "basis/languagesupport");
	}

	
	public static URL[] testSupportJars(File root) {
		return findJars(root, "basis/testsupport");
	}

	
	private static URL[] findJars(File root, String relative) {
		File jarRoot = new File(root, relative);
		List<URL> jarURLs = new ArrayList<URL>();
		LinkedList<File> folderQueue = new LinkedList<File>();
		folderQueue.add(jarRoot);
		while(!folderQueue.isEmpty()) {
			File curFolder = folderQueue.removeFirst();
			File[] curJars = curFolder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.isFile() && file.getName().endsWith(".jar");
				}
			});
			for (File curJar : curJars) {
				jarURLs.add(toURL(curJar));
			}
			File[] subFolders = curFolder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.isDirectory() && !file.getName().startsWith(".");
				}
			});
			for (File subFolder : subFolders) {
				folderQueue.add(subFolder);
			}
		}
		return jarURLs.toArray(new URL[jarURLs.size()]);
	}
	
	private static URL toURL(File file) {
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

}
