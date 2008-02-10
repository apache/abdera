package org.apache.abdera.spring;


public class ClassicProviderDefinitionParserTest extends ProviderDefinitionParserTest {
			
	@Override
    protected String getConfigPath() {
        return "/org/apache/abdera/spring/classicBeansDefinition.xml";
    }
}
