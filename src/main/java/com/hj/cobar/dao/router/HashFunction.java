package com.hj.cobar.dao.router;

public class HashFunction{
	
	public int apply(Long taobaoId) {
		System.out.println("taobaoId："+taobaoId);
		int result = (int)(taobaoId % 3);
		return result;
	}
}
