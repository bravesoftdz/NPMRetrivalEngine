package test;

import java.net.InetSocketAddress;
import java.net.Proxy;

import external.wrappers.NPMReadmeWrapper;

    

public class TestNPMReadmeWrapper {

	public static void main(String[] args) {
		
		Proxy proxy = new Proxy(                                      //
			    Proxy.Type.HTTP,                                      //
			    InetSocketAddress.createUnresolved("192.168.2.12", 3128) //
			);
		
		NPMReadmeWrapper rw = new NPMReadmeWrapper();
		String result = rw.downloadReadmeContent("lodash", proxy);
		System.out.println(result);
		
		

	}

}
