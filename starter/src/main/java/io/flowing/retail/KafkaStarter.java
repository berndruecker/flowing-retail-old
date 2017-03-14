package io.flowing.retail;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

import kafka.server.KafkaServerStartable;

public class KafkaStarter {
  
  private static class StoppableZooKeeperServerMain extends ZooKeeperServerMain {
    
    public void stop() {
      shutdown();
    }
    
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    File fileKafkaLogs = new File("data/kafka-logs");
    File fileZookeeperData = new File("data/zookeeper");
    File fileZookeeperDataLogs = new File("data/zookeeper-logs");
    
    
    final StoppableZooKeeperServerMain zookeeper = new StoppableZooKeeperServerMain();
    
    final ServerConfig zooKeeperConfig = new ServerConfig();
    QuorumPeerConfig quorumPeerConfig = new QuorumPeerConfig() {
      public String getDataDir() {
        return fileZookeeperData.getAbsolutePath();
      }
      public String getDataLogDir() {
        return fileZookeeperDataLogs.getAbsolutePath();
      }
      public InetSocketAddress getClientPortAddress() {
        return new InetSocketAddress(2181);
      }     
    };
    zooKeeperConfig.readFrom(quorumPeerConfig);
    Thread zookeeperThread = new Thread() {
      @Override
      public void run() {
        try {
          zookeeper.runFromConfig(zooKeeperConfig);
        } catch (IOException e) {
          throw new RuntimeException("Could not start Zookeeper: " + e.getMessage(), e);
        } 
      }      
    };
    zookeeperThread.start();
    
    Properties kafkaProperties = new Properties();    
    kafkaProperties.put("zookeeper.connect", "localhost:2181");
    kafkaProperties.put("broker.id", "1");
    kafkaProperties.put("log.dirs",  fileKafkaLogs.getAbsolutePath());    
    
    final KafkaServerStartable kafka = KafkaServerStartable.fromProps(kafkaProperties);
//    zookeeperThread.join();
    kafka.startup();
    

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          kafka.shutdown();
          kafka.awaitShutdown();
          zookeeper.stop();
        } catch (Exception e) {
          throw new RuntimeException("Could not disconnect: " + e.getMessage(), e);
        }
      }
    });

  }

}
