/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.foi.nwtis.tjukica.web.slusaci;


import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.tjukica.ejb.eb.Mqtt;
import org.foi.nwtis.tjukica.ejb.sb.MqttFacadeLocal;
import org.foi.nwtis.tjukica.konfiguracije.bp.BP_Konfiguracija;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;


/**
 * Uses an callback based interface to MQTT. Callback based interfaces are
 * harder to use but are slightly more efficient.
 */
public class MqttListener extends Thread {

    static BP_Konfiguracija konf;
    MqttFacadeLocal mqttFacade;
    
    public MqttListener(BP_Konfiguracija k, MqttFacadeLocal facade) throws Exception {
        MqttListener.konf = k;
        mqttFacade = facade;
        
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        try {
            String user = konf.getKonfig().dajPostavku("mqtt.korisnik");
            String password = konf.getKonfig().dajPostavku("mqtt.lozinka");
            String host = konf.getKonfig().dajPostavku("mqtt.host");
            int port = Integer.parseInt(konf.getKonfig().dajPostavku("mqtt.port"));
            final String destination = konf.getKonfig().dajPostavku("mqtt.destination");
            
            MQTT mqtt = new MQTT();
            mqtt.setHost(host, port);
            mqtt.setUserName(user);
            mqtt.setPassword(password);
            
            final CallbackConnection connection = mqtt.callbackConnection();
            connection.listener(new org.fusesource.mqtt.client.Listener() {
                long count = 0;
                
                @Override
                public void onConnected() {
                    System.out.println("Otvorena veza na MQTT");
                }
                
                @Override
                public void onDisconnected() {
                    System.out.println("Prekinuta veza na MQTT");
                    System.exit(0);
                }
                
                @Override
                public void onFailure(Throwable value) {
                    System.out.println("Problem u vezi na MQTT");
                    System.exit(-2);
                }
                
                @Override
                public void onPublish(UTF8Buffer topic, Buffer msg, Runnable ack) {
                    String body = msg.utf8().toString();
                    Mqtt m = new Mqtt();
                    m.setKorisnik(topic.toString());
                    m.setPoruka(body);
                    Timestamp t = new Timestamp(System.currentTimeMillis());
                    m.setStored(t);                   
                    m.setId(0);
                    mqttFacade.create(m);
                    System.out.println("Stigla poruka br: " + count);
                    count++;
                }
            });
            connection.connect(new Callback<Void>() {
                @Override
                public void onSuccess(Void value) {
                    Topic[] topics = {new Topic(destination, QoS.AT_LEAST_ONCE)};
                    connection.subscribe(topics, new Callback<byte[]>() {
                        @Override
                        public void onSuccess(byte[] qoses) {
                            System.out.println("Pretplata na: " + destination);
                        }
                        
                        @Override
                        public void onFailure(Throwable value) {
                            System.out.println("Problem kod pretplate na: " + destination);
                            System.exit(-2);
                        }
                    });
                }
                
                @Override
                public void onFailure(Throwable value) {
                    System.out.println("Neuspjela pretplata na: " + destination);
                    System.exit(-2);
                }
            });
            
            // Wait forever..
            synchronized (MqttListener.class) {
                while (true) {
                    MqttListener.class.wait();
                }
            }
        } catch (URISyntaxException | InterruptedException ex) {
            Logger.getLogger(MqttListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized void start() {
        
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }
}
