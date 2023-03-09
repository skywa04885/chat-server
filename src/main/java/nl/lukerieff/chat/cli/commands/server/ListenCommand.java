package nl.lukerieff.cli.commands.server;

import nl.lukerieff.Main;
import nl.lukerieff.TokenAuthenticator;
import nl.lukerieff.cli.commands.UserCommand;
import nl.lukerieff.protocol.channel.ChannelOptions;
import nl.lukerieff.protocol.channel.authentication.Authenticator;
import nl.lukerieff.protocol.channel.events.EventListeners;
import nl.lukerieff.protocol.channel.services.Services;
import nl.lukerieff.protocol.server.Server;
import nl.lukerieff.services.UserServices;
import picocli.CommandLine;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.KeyStore;

@CommandLine.Command(
        name = "listen",
        subcommands = {
                UserCommand.class
        }
)
public class ListenCommand implements Runnable {
    private SSLContext createSSLContext() throws Exception {
        final KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream("/home/luke/keys/test.jks"), "Ffeirluke234".toCharArray());

        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, "Ffeirluke234".toCharArray());
        final KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keyStore);
        final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        final SSLContext sslContext = SSLContext.getInstance("TLSv1");
        sslContext.init(keyManagers, trustManagers, null);

        return sslContext;
    }

    @Override
    public void run() {
        try {
            final SSLContext sslContext = createSSLContext();
            final SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();

            final Authenticator channelOptionsAuthenticator = new TokenAuthenticator(Main.tokenSigner);
            final ChannelOptions channelOptions = ChannelOptions
                    .newBuilder()
                    .setAuthenticator(channelOptionsAuthenticator)
                    .build();

            final Services services = new Services();
            services.add(UserServices.ME);
            services.add(UserServices.SEARCH);

            final EventListeners channelEventListeners = new EventListeners();

            final Server rockSockServer = Server
                    .newBuilder()
                    .setChannelOptions(channelOptions)
                    .setEventListeners(channelEventListeners)
                    .setServices(services)
                    .build(sslServerSocketFactory);

            final Thread thread = new Thread(rockSockServer);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
