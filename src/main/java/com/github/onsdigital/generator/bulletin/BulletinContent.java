package com.github.onsdigital.generator.bulletin;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.generator.CSV;
import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.bulletin.Bulletin;

public class BulletinContent {

	private static List<Map<String, String>> rows;

	public static List<Bulletin> getBulletins(Folder folder) throws IOException {
		List<Bulletin> result = null;

		// Parse the data:
		if (rows == null) {
			parseCsv();
		}

		BulletinNode node = getNode(folder);
		if (node != null) {
			result = node.bulletinList().bulletins;
		}

		return result;
	}

	private static BulletinNode getNode(Folder folder) {
		BulletinNode result = null;

		// Recurse up the hierarchy to the root node:
		BulletinNode parentNode = null;
		if (folder.parent == null) {
			parentNode = BulletinData.rootNode;
		} else {
			parentNode = getNode(folder.parent);
		}

		// Get the matching node:
		if (parentNode != null) {
			result = parentNode.getChild(folder.name);
		}

		return result;
	}

	private static void parseCsv() throws IOException {
		rows = CSV.parse("/Alpha bulletin content.csv");
		// String[] headings = { "Theme", "Level 2", "Level 3", "Name", "Key",
		// "Units", "CDID", "Path", "Link", "Notes" };

		for (Map<String, String> row : rows) {

			// There are blank rows separating the themes:
			if (StringUtils.isBlank(row.get("Theme"))) {
				continue;
			}

			// Get to the folder in question:
			BulletinNode node = BulletinData.rootNode.getChild(row.get("Theme"));
			if (StringUtils.isNotBlank(row.get("Level 2"))) {
				node = node.getChild(row.get("Level 2"));
			}
			if (StringUtils.isNotBlank(row.get("Level 3"))) {
				node = node.getChild(row.get("Level 3"));
			}

			Bulletin bulletin = new Bulletin();
			bulletin.name = StringUtils.trim(row.get("Name"));
			bulletin.title = bulletin.name;
			bulletin.fileName = bulletin.name.toLowerCase();
			node.addBulletin(bulletin);
			BulletinData.bulletins.add(bulletin);
		}
	}

	public static void main(String[] args) throws IOException {
		Folder theme = new Folder();
		theme.name = "Business, Industry and Trade";
		Folder level2 = new Folder();
		level2.name = "Business Activity, Size and Location";
		level2.parent = theme;
		Folder level3 = new Folder();
		level3.name = "Activity, Size and Location";
		level3.parent = level2;
		System.out.println(getBulletins(level3));
	}
}
