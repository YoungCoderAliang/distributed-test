package org.distributed.test.service;

import java.io.Serializable;

public class SimpleResponse<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private String result = "succ";
	private String msg;
	private T data;
	
	public static class Result {
		public static String success = "succ";
		public static String error = "error";
		public static String needlogin = "needlogin";
		public static String sessionOutdate = "sessionOutdate";
		public static String needPicCode = "needPicCode";
		public static String forceUpdate = "forceUpdate";
		public static String suggestUpdate = "suggestUpdate";
	}
	
	public static class Msg {
		public static final String PERMISSION_NOT_ENOUGH = "您不具备此操作权限";
		public static final String ROOM_NOT_EXIST = "房间不存在";
	}

	public SimpleResponse() {
	}

	public SimpleResponse(T data) {
		this.data = data;
	}

	public boolean success() {
		return result.equals("succ");
	}

	public void setError(String msg) {
		this.result = "error";
		this.msg = msg;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
