package io.magician.common.util;

import io.magician.common.constant.CommonConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * read class file
 * 
 * @author yuye
 *
 */
public class ReadClassUtil {
	
	private static Logger log = LoggerFactory.getLogger(ReadClassUtil.class);
	

	/**
	 * Get all classes under a package (including all subpackages of the package)
	 *
	 * @param packageName
	 * @return the full name of the class
	 * @throws UnsupportedEncodingException
	 */
	public static Set<String> loadClassList(String packageName) throws IOException {
		if(packageName == null) {
			return new LinkedHashSet<>();
		}
		return getClasses(packageName);
	}

	/**
	 * Get all the Classes from the package
	 * 
	 * @param pack
	 * @return
	 */
	private static Set<String> getClasses(String pack) {

		// A collection of all classes
		Set<String> classes = new LinkedHashSet<String>();
		// whether to iterate
		boolean recursive = true;
		// Get the name of the package and replace it
		String packageName = pack;
		String packageDirName = packageName.replace('.', '/');
		// Define an enumerated collection and loop through the things in this directory
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				// get the next element
				URL url = dirs.nextElement();
				// get the name of the protocol
				String protocol = url.getProtocol();
				// If it is saved as a file on the server
				if ("file".equals(protocol)) {
					// Get the physical path of the package
					String filePath = URLDecoder.decode(url.getFile(), CommonConstant.ENCODING);
					// Scan files under the entire package as files and add to the collection
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
				} else if ("jar".equals(protocol)) {
					// Define a JarFile
					JarFile jar;
					try {
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						// Get an enumeration class from this jar
						Enumeration<JarEntry> entries = jar.entries();

						while (entries.hasMoreElements()) {
							// Get an entity in the jar, which can be a directory and some other files in the jar package such as META-INF and other files
							JarEntry entry = entries.nextElement();
							String name = entry.getName();

							if (name.charAt(0) == '/') {
								name = name.substring(1);
							}
							// If the first half is the same as the defined package name
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								if (idx != -1) {
									// Get package name Replace "/" with "."
									packageName = name.substring(0, idx).replace('/', '.');
								}
								// If it can iterate and is a package
								if ((idx != -1) || recursive) {
									// If it's a .class file instead of a directory
									if (name.endsWith(".class") && !entry.isDirectory()) {
										// Remove the trailing ".class" to get the real class name
										String className = name.substring(packageName.length() + 1, name.length() - 6);
										classes.add(packageName + '.' + className);
									}
								}
							}
						}
					} catch (IOException e) {
						log.error("Error getting files from jar package while scanning user-defined views",e);
					}
				}
			}
		} catch (IOException e) {
			log.error("An error occurred while scanning the classes under the [" + packageName + "] package",e);
		}

		return classes;
	}

	/**
	 * Get all the Classes under the package in the form of a file
	 * 
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive,
			Set<String> classes) {
		// Get the directory of this package Create a File
		File dir = new File(packagePath);
		// If it does not exist or is not a directory, it will return directly
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		// If it exists, get all files under the package, including directories
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// Custom filtering rules If you can loop (including subdirectories) or files ending with .class (compiled java class files)
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});

		for (File file : dirfiles) {
			// If it is a directory, continue scanning
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
						classes);
			} else {
				// If it is a java class file, remove the following .class and leave only the class name
				String className = file.getName().substring(0, file.getName().length() - 6);
				classes.add(packageName + '.' + className);
			}
		}
	}
}
