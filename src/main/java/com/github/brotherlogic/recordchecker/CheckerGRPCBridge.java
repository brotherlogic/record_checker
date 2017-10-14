package com.github.brotherlogic.recordchecker;

import java.util.List;

import discogsserver.DiscogsServiceGrpc;
import discogsserver.DiscogsServiceGrpc.DiscogsServiceBlockingStub;
import discogsserver.Server.Empty;
import discogsserver.Server.MetadataUpdate;
import discogsserver.Server.ReleaseList;
import discogsserver.Server.ReleaseMetadata;
import godiscogs.Godiscogs.Release;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CheckerGRPCBridge implements CheckerBridge {

	DiscogsServiceBlockingStub stub;

	@Override
	public void connect(String host, int port) {
		ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build();
		stub = DiscogsServiceGrpc.newBlockingStub(channel);
	}

	@Override
	public List<Release> getReleases() {
		System.out.println("MAKING THE CALL");
		ReleaseList list = stub.getIncompleteReleases(Empty.getDefaultInstance());

		return list.getReleasesList();
	}

	@Override
	public List<Release> getPurchases() {
		ReleaseList list = stub.getIncompleteReleases(Empty.getDefaultInstance());
		System.out.println("RETURN: " + list.getReleasesCount());
		return list.getReleasesList();
	}

	@Override
	public ReleaseMetadata getMetadata(Release r) {
		return stub.getMetadata(r);
	}

	@Override
	public void setFilePath(Release rel, String path) {
		ReleaseMetadata toUpdate = ReleaseMetadata.newBuilder().setFilePath(path).build();
		MetadataUpdate update = MetadataUpdate.newBuilder().setRelease(rel).setUpdate(toUpdate).build();
		stub.updateMetadata(update);
	}

	@Override
	public void setCost(Release rel, int cost) {
		ReleaseMetadata toUpdate = ReleaseMetadata.newBuilder().setCost(cost).build();
		MetadataUpdate update = MetadataUpdate.newBuilder().setRelease(rel).setUpdate(toUpdate).build();
		stub.updateMetadata(update);
	}

}
