/*
 * Copyright (C) 2015 SDN-WISE
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.sdnwiselab.sdnwise.application;

import com.github.sdnwiselab.sdnwise.adapter.Adapter;
import com.github.sdnwiselab.sdnwise.controller.Controller;
import com.github.sdnwiselab.sdnwise.controlplane.ControlPlaneLayer;
import com.github.sdnwiselab.sdnwise.packet.DataPacket;
import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import static com.github.sdnwiselab.sdnwise.packet.NetworkPacket.SDN_WISE_DATA;
import com.github.sdnwiselab.sdnwise.topology.NetworkGraph;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Representation of the sensor network and resolver of all the data packets
 * coming from the network itself. This abstract class has two main private
 * methods. managePacket and graphUpdate(abstract). The first is called when a
 * request is coming from the network while the latter is called when something
 * in the topology of the network changes.
 * <p>
 * There are a send and a receive methods to/from the Adaptation Layer
 *
 * @author Sebastiano Milardo
 */
//public abstract class AbstractApplication extends ControlPlaneLayer {
public abstract class AbstractApplication_Old extends ControlPlaneLayer {

    /**
     * To avoid garbage collection of the logger.
     */
    protected static final Logger LOGGER = Logger.getLogger("APP");
    /**
     * Incomig packets queue size.
     */
    private static final int QUEUE_SIZE = 1000;
    /**
     * Incoming packets queue.
     */
    private final ArrayBlockingQueue<NetworkPacket> bQ;
    /**
     * Controller.
     */
    private final Controller controller;

    /**
     * Creates an Application Abstract Class.
     *
     * @param ctrl the controller to be used
     * @param lower the adapter to be used
     */
    public AbstractApplication_Old(final Controller ctrl,
            final List<Adapter> lower) {
        super("APP", lower, null);
        //ControlPlaneLogger.setupLogger(getLayerShortName());
        controller = ctrl;
        bQ = new ArrayBlockingQueue<>(QUEUE_SIZE);
    }

    /**
     * Gets the Network Graph of this AbstractController.
     *
     * @return the controller network graph.
     */
    public final NetworkGraph getNetworkGraph() {
        return controller.getNetworkGraph();
    }

    /**
     * Invoked every time there is a change in the topology of the network. A
     * change can be an added/removed node/link or a variation in the rssi
     * between nodes. The granularity of this updates can be specified in the
     * NetworkGraph class.
     */
    public abstract void graphUpdate();

    /**
     * Invoked every time a DataPacket is received by the application.
     *
     * @param data incoming DataPacket.
     */
    public abstract void receivePacket(DataPacket data);

    /**
     * Sends a generic message to a node. The message is represented by an array
     * of bytes.
     *
     * @param net network id of the dst node
     * @param dst network address of the dst node
     * @param message the content of the message to be sent
     */
    public final void sendMessage(final byte net, final NodeAddress dst,
            final byte[] message) {
        if (message.length != 0) {
            //DataPacket dp = new DataPacket(net, controller.getSinkAddress(), dst, message);
            DataPacket dp = new DataPacket(net, controller.getSinkAddress(), dst);
            dp.setNxhop(controller.getSinkAddress());
            getLower().stream().forEach((adapter) -> {
                adapter.send(dp.toByteArray());
            });
        }
    }

    /**
     * Sends a generic message to a node. The message is represented by string.
     *
     * @param net network id of the destination node
     * @param destination network address of the destination node
     * @param message the content of the message to be sent
     */
    public final void sendMessage(final byte net, final NodeAddress destination,
            final String message) {
        if (message != null && !message.isEmpty()) {
            sendMessage(net, destination, message.getBytes(UTF8_CHARSET));
        }
    }

    @Override
    public final void update(final Observable o, final Object arg) {
        if (o.equals(getLower())) {
            try {
                bQ.put(new NetworkPacket((byte[]) arg));
            } catch (InterruptedException ex) {
                log(Level.SEVERE, ex.toString());
            }
        }
    }

    @Override
    protected final void setupLayer() {
        new Thread(new Worker()).start();
    }

    /**
     * Extracts the packets from the incoming queue and send them to the
     * receivePacket function.
     */
    private class Worker implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    NetworkPacket data = bQ.take();
                    if (data.getType() == SDN_WISE_DATA) {
                        receivePacket(new DataPacket(data));
                    }
                } catch (InterruptedException ex) {
                    log(Level.SEVERE, ex.toString());
                }
            }
        }
    }
}