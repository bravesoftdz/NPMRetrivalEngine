package test;

import java.net.InetSocketAddress;
import java.net.Proxy;

import external.wrappers.NPMUtilWrapper;

    

public class TestNPMReadmeWrapper {

	public static void main(String[] args) {
		
		Proxy proxy = new Proxy(                                      //
			    Proxy.Type.HTTP,                                      //
			    InetSocketAddress.createUnresolved("192.168.2.12", 3128) //
			);
		
		NPMUtilWrapper rw = new NPMUtilWrapper();
		String result = rw.downloadReadmeContent("lodash", proxy);
		System.out.println(result);
		
		

	}

}
