module com.eisgroup.tooling.javadoc.doclet {
	requires jdk.javadoc;
	requires org.apache.commons.io;
	requires org.apache.commons.lang3;
	requires org.slf4j;
	requires doclet;
	requires org.jsoup;
	
	exports com.exigen.publicapi.doclet;
	exports com.exigen.techdoc.doclet;
	exports com.exigen.techdoc.doclet.filter;
}