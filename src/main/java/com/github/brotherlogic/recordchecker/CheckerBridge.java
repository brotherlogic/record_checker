package com.github.brotherlogic.recordchecker;

import java.util.List;

import discogsserver.Server.ReleaseMetadata;
import godiscogs.Godiscogs.Release;

public interface CheckerBridge {
	List<Release> getReleases();

	List<Release> getPurchases();

	ReleaseMetadata getMetadata(Release r);

	void connect(String host, int port);

	void setFilePath(Release rel, String path);

	void setCost(Release rel, int cost);
}
