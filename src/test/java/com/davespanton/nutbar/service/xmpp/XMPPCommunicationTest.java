package com.davespanton.nutbar.service.xmpp;

import com.davespanton.nutbar.injected.InjectedTestRunner;
import com.davespanton.nutbar.shadows.ShadowChat;
import com.davespanton.nutbar.shadows.ShadowChatManager;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.xtremelabs.robolectric.Robolectric;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.*;

@RunWith(InjectedTestRunner.class)
public class XMPPCommunicationTest {

    private static final String USERNAME = "some.user";
    private static final String PASSWORD = "password";

    private static final String TEST_MESSAGE = "message";
    @Inject
    private XMPPCommunication xmppCommunication;

    @Inject
    private Provider<XMPPConnection> provider;

    private StubXMPPConnection xmppConnection;

    @Before
    public void setup() {
        xmppConnection = (StubXMPPConnection) provider.get();
        xmppCommunication.connect(USERNAME, PASSWORD);
    }

    @Test
    public void shouldConnectToXmppServer() {
        assertTrue(xmppConnection.isConnected());
    }

    @Test
    public void shouldLoginOnConnectionWhenCredentialsAreFound() {
        assertEquals(USERNAME, xmppConnection.getUsername());
        assertEquals(PASSWORD, xmppConnection.getPassword());
    }

    @Test
    public void shouldDisconnectFromXMPPConnection() {
        xmppCommunication.disconnect();
        assertFalse(xmppConnection.isConnected());
    }

    @Test
    public void shouldInitiateChatOnLogin() {
        assertTrue(getShadowChatManager().hasCreatedChatForRecipient(XMPPCommunication.XMPP_RECIPIENT));
    }

    private ShadowChatManager getShadowChatManager() {
        return Robolectric.shadowOf_(xmppConnection.getChatManager());
    }

    @Test
    public void shouldSendAMessageWhenConnected() {
        xmppCommunication.sendMessage(TEST_MESSAGE);

        assertTrue(getShadowChatForXmppRecipient().hasSentMessage(TEST_MESSAGE));
    }

    private ShadowChat getShadowChatForXmppRecipient() {
        Chat chat = getShadowChatManager().getChatForRecipient(XMPPCommunication.XMPP_RECIPIENT);
        return Robolectric.shadowOf_(chat);
    }

    @Test
    public void shouldNotSendAMessageWhenNotConnected() {
        xmppCommunication.disconnect();
        xmppCommunication.sendMessage(TEST_MESSAGE);

        assertFalse(getShadowChatForXmppRecipient().hasSentMessage(TEST_MESSAGE));
    }

    @Test
    public void shouldReturnIsConnectedWhenConnected() throws XMPPException {
        xmppConnection.connect();
        assertTrue(xmppCommunication.isConnected());
    }
}
