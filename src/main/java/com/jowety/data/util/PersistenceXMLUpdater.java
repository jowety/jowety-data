package com.jowety.data.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import jakarta.persistence.Converter;
import jakarta.persistence.Entity;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.jowety.util.ClasspathScanner;

public class PersistenceXMLUpdater {

	private SAXBuilder builder = new SAXBuilder(false);
	private XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

	public PersistenceXMLUpdater() {
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory","org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
		System.setProperty("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");
		builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
	}

	/**
	 * Deletes and replaces all the class elements in the persistence.xml file with classes found
	 * by scanning the input packages.
	 * @param filepath The full path to the persistence.xml file to update
	 * @param packages java packages names to scan for @Entity classes
	 */
	public void updatePersistenceXMLFile(String filepath, String[] packages) {

		try {
			File file = new File(filepath);
			Document doc = builder.build(file);
			Element root = doc.getRootElement();
			Namespace ns = root.getNamespace();
			Element pu = root.getChild("persistence-unit", ns);

			//remove all the class elements
			pu.removeContent(new ElementFilter("class"));
			//remove and keep the elements following class
			Element excludeUnlistedClasses = pu.getChild("exclude-unlisted-classes", ns);
			if(excludeUnlistedClasses!=null) pu.removeContent(excludeUnlistedClasses);
			Element sharedCacheMode = pu.getChild("shared-cache-mode", ns);
			if(sharedCacheMode!=null) pu.removeContent(sharedCacheMode);
			Element validationMode = pu.getChild("validation-mode", ns);
			if(validationMode!=null) pu.removeContent(validationMode);
			Element properties = pu.getChild("properties", ns);
			if(properties!=null) pu.removeContent(properties);

			//add class elements by scanning the packages
			for(String pkg: packages) {
				List<Class> classes = getEntityClasses(pkg);
				for(Class c: classes) {
					Element cEl = new Element("class", ns);
					cEl.setText(c.getName());
					pu.addContent(cEl);
				}
			}
			//add back the elements that come after class
			if(excludeUnlistedClasses!=null) pu.addContent(excludeUnlistedClasses);
			if(sharedCacheMode!=null) pu.addContent(sharedCacheMode);
			if(validationMode!=null) pu.addContent(validationMode);
			if(properties!=null) pu.addContent(properties);

			System.out.println("Writing updated xml to : " + file.getAbsolutePath());
			FileOutputStream fos = new FileOutputStream(file);
			outputter.output(doc, fos);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public  List<Class> getEntityClasses(String packageName) throws ClassNotFoundException, IOException{
		List<Class> out = new ClasspathScanner().matchPackage(packageName).matchAnnotation(Entity.class).find();
		out.addAll(new ClasspathScanner().matchPackage(packageName).matchAnnotation(Converter.class).find());
		return out;

	}

}
