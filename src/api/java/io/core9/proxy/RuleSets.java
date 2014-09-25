package io.core9.proxy;

import java.util.List;

public class RuleSets {

	private List<String> preRequest;
	private List<String> postResponse;

	public List<String> getPreRequest() {
		return preRequest;
	}

	public void setPreRequest(List<String> preRequest) {
		this.preRequest = preRequest;
	}

	public List<String> getPostResponse() {
		return postResponse;
	}

	public void setPostResponse(List<String> postResponse) {
		this.postResponse = postResponse;
	}

}
