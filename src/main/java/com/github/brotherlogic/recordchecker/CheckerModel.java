package com.github.brotherlogic.recordchecker;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import discogsserver.Server.ReleaseMetadata;
import godiscogs.Godiscogs.Format;
import godiscogs.Godiscogs.Release;

public class CheckerModel {

	private CheckerBridge bridge;
	private Set<String> seen = new TreeSet<String>();

	public CheckerModel(CheckerBridge bridge) {
		this.bridge = bridge;
	}

	public void connect(String host, int port) {
		bridge.connect(host, port);
	}

	public List<String> getMissing() {
		List<String> missing = new LinkedList<String>();
		for (File f : new File("/Users/simon/Music/iTunes/iTunes Media/Music/").listFiles()) {
			if (f.isDirectory())
				for (File f2 : f.listFiles())
					missing.add(f.getName() + "/" + f2.getName());
		}

		List<String> remove = new LinkedList<String>();
		for (String added : missing) {
			boolean found = false;
			for (String str : seen) {
				if (str.toLowerCase().equals(added.toLowerCase()))
					found = true;

			}
			if (found)
				remove.add(added);
		}
		missing.removeAll(remove);
		return missing;
	}

	public Release getReleaseToUpdate() {
		Release needsCheck = null;

		List<Release> releases = bridge.getReleases();
		int count = 0;
		for (Release rel : releases) {
			count++;
			ReleaseMetadata meta = bridge.getMetadata(rel);
			if (meta.getFilePath().length() == 0
					|| !new File("/Users/simon/Music/iTunes/iTunes Media/Music/" + meta.getFilePath()).exists()) {
				// If this is in the listening box and is not a CD, ignore
				boolean cd = false;
				for (Format format : rel.getFormatsList())
					if (format.getName().contains("CD"))
						cd = true;

				System.out.println("HERE = " + rel.getTitle() + " => " + cd + " and " + rel.getFolderId() + " FROM "
						+ rel.getFormatsList() + " BUUUUT " + rel);
				if ((rel.getFolderId() != 673768 && rel.getFolderId() != 812802) || cd)
					return rel;
			}
			if (meta.getFilePath().endsWith("/"))
				seen.add(meta.getFilePath().substring(0, meta.getFilePath().length() - 1));
			else
				seen.add(meta.getFilePath());
		}

		return needsCheck;
	}

	public Release getReleaseToCost() {
		// 01/10/16
		long startDate = 1475280000;
		List<Release> releases = bridge.getPurchases();
		int count = 0;
		for (Release rel : releases) {
			ReleaseMetadata meta = bridge.getMetadata(rel);
			if (meta.getCost() == 0 && meta.getDateAdded() > startDate) {
				return rel;
			}
			count++;
		}

		return null;
	}

	public void setFilePath(Release r, String filePath) {
		bridge.setFilePath(r, filePath);
	}

	public void setCost(Release r, int cost) {
		bridge.setCost(r, cost);
	}

}
