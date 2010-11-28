package org.apache.abdera.protocol.server.processors;

import static org.junit.Assert.*;

import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.easymock.EasyMock;
import org.junit.*;

public class EntryRequestProcessorTest {

        private EntryRequestProcessor processor = new EntryRequestProcessor();
        private CollectionAdapter adapterMock = EasyMock.createStrictMock(CollectionAdapter.class);
        private RequestContext requestMock = EasyMock.createStrictMock(RequestContext.class);
        private ResponseContext responseMock = EasyMock.createStrictMock(ResponseContext.class);
        
        @After
        public void verify() throws Exception {
                EasyMock.verify(adapterMock);
                EasyMock.verify(requestMock);
                EasyMock.verify(responseMock);
        }
        
        @Test
        public void testProcessEntryGet() {
                EasyMock.replay(responseMock);
                
                EasyMock.expect(requestMock.getMethod()).andReturn("GET");
                EasyMock.replay(requestMock);
                
                EasyMock.expect(adapterMock.getEntry(requestMock)).andReturn(responseMock);
                EasyMock.replay(adapterMock);
                
                ResponseContext response = processor.processEntry(requestMock, adapterMock);
                assertNotNull(response);
        }
        
        @Test
        public void testProcessEntryPost() {
                EasyMock.replay(responseMock);
                
                EasyMock.expect(requestMock.getMethod()).andReturn("POST");
                EasyMock.replay(requestMock);
                
                EasyMock.expect(adapterMock.postEntry(requestMock)).andReturn(responseMock);
                EasyMock.replay(adapterMock);
                
                ResponseContext response = processor.processEntry(requestMock, adapterMock);
                assertNotNull(response);
        }
        
        @Test
        public void testProcessEntryPut() {
                EasyMock.replay(responseMock);
                
                EasyMock.expect(requestMock.getMethod()).andReturn("PUT");
                EasyMock.replay(requestMock);
                
                EasyMock.expect(adapterMock.putEntry(requestMock)).andReturn(responseMock);
                EasyMock.replay(adapterMock);
                
                ResponseContext response = processor.processEntry(requestMock, adapterMock);
                assertNotNull(response);
        }
        
        @Test
        public void testProcessEntryDelete() {
                EasyMock.replay(responseMock);
                
                EasyMock.expect(requestMock.getMethod()).andReturn("DELETE");
                EasyMock.replay(requestMock);
                
                EasyMock.expect(adapterMock.deleteEntry(requestMock)).andReturn(responseMock);
                EasyMock.replay(adapterMock);
                
                ResponseContext response = processor.processEntry(requestMock, adapterMock);
                assertNotNull(response);
        }
        
        @Test
        public void testProcessEntryHead() {
                EasyMock.replay(responseMock);
                
                EasyMock.expect(requestMock.getMethod()).andReturn("HEAD");
                EasyMock.replay(requestMock);
                
                EasyMock.expect(adapterMock.headEntry(requestMock)).andReturn(responseMock);
                EasyMock.replay(adapterMock);
                
                ResponseContext response = processor.processEntry(requestMock, adapterMock);
                assertNotNull(response);
        }
        
        @Test
        public void testProcessEntryOptions() {
                EasyMock.replay(responseMock);
                
                EasyMock.expect(requestMock.getMethod()).andReturn("OPTIONS");
                EasyMock.replay(requestMock);
                
                EasyMock.expect(adapterMock.optionsEntry(requestMock)).andReturn(responseMock);
                EasyMock.replay(adapterMock);
                
                ResponseContext response = processor.processEntry(requestMock, adapterMock);
                assertNotNull(response);
        }
        
        
}
