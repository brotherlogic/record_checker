package com.github.brotherlogic.recordchecker;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import com.github.brotherlogic.javaserver.JavaServer;

import godiscogs.Godiscogs.Release;
import io.grpc.BindableService;

public class Checker extends JavaServer {

	@Override
	public String getServerName() {
		return "RecordChecker";
	}

	@Override
	public List<BindableService> getServices() {
		return new LinkedList<BindableService>();
	}

	@Override
	public void localServe() {
		model.connect(getHost("discogssyncer"), getPort("discogssyncer"));

		Release r = model.getReleaseToCost();
		while (r != null) {
			String answer = JOptionPane
					.showInputDialog(r.getArtists(0).getName() + " - " + r.getTitle() + " [" + r.getFolderId() + "]");
			model.setCost(r, Integer.parseInt(answer));
			r = model.getReleaseToCost();
		}

		r = model.getReleaseToUpdate();
		while (r != null) {
			// See if we can find the path manually
			File f = new File("/Users/simon/Music/iTunes/iTunes Media/Music/"
					+ r.getArtists(0).getName().replace("Various", "Compilations") + "/" + r.getTitle());
			if (f.exists()) {
				System.out.println("Setting automatically");
				model.setFilePath(r, r.getArtists(0).getName().replace("Various", "Compilations") + "/" + r.getTitle());
			} else {
				String answer = JOptionPane.showInputDialog(r.getArtists(0).getName() + " - " + r.getTitle());
				model.setFilePath(r, answer);
			}
			r = model.getReleaseToUpdate();
		}

		// Print out all the missing records
		List<String> blah = model.getMissing();
		Collections.shuffle(blah);
		for (String missing : blah) {
			System.out.println("Missing = " + missing);
			System.exit(1);
		}

	}

	CheckerModel model;

	public Checker(CheckerModel model) {
		this.model = model;
	}

	public void printRelease() {
		Release rel = model.getReleaseToUpdate();
		System.out.println(rel.getArtists(0).getName() + " - " + rel.getTitle());
	}

	public static void main(String[] args) {
		final Checker checker = new Checker(new CheckerModel(new CheckerGRPCBridge()));
		checker.Serve("192.168.86.64", 50055);
	}
}
