package org.opentripplanner.api.thrift.util;

import java.util.Set;

import org.opentripplanner.api.thrift.OTPServerTask;
import org.opentripplanner.api.thrift.definition.LatLng;
import org.opentripplanner.api.thrift.definition.Location;
import org.opentripplanner.api.thrift.definition.TravelMode;
import org.opentripplanner.api.thrift.definition.TripParameters;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NoArgsConstructor;

/**
 * Builder for RoutingRequests.
 * 
 * @author avi
 */
@NoArgsConstructor
public class RoutingRequestBuilder {

	private static Logger LOG = LoggerFactory.getLogger(OTPServerTask.class);

	private final RoutingRequest routingRequest = new RoutingRequest();
	private Graph graph;

	/**
	 * Initialize with given TripParameters.
	 * 
	 * @param tripParams
	 */
	public RoutingRequestBuilder(TripParameters tripParams) {
		addTripParameters(tripParams);
	}

	/**
	 * Convert a LatLng structure into an internal String representation.
	 * 
	 * TODO(flamholz): put this on something inheriting from LatLng?
	 * 
	 * @param latlng
	 * @return String that is accepted internally as a LatLng.
	 */
	private static String latLngToString(final LatLng latlng) {
		// NOTE: 7 decimal places means better than cm resolution.
		return String.format("%.7f,%.7f", latlng.getLat(), latlng.getLng());
	}

	/**
	 * Adds TripParameters to the RoutingRequest.
	 * 
	 * @param tripParams
	 * @return self reference
	 */
	public RoutingRequestBuilder addTripParameters(TripParameters tripParams) {
		if (tripParams.isSetAllowed_modes()) {
			Set<TravelMode> allowedModes = tripParams.getAllowed_modes();
			setTravelModes(new TravelModeSet(allowedModes));
		}

		setOrigin(tripParams.getOrigin().getLat_lng());
		setDestination(tripParams.getDestination().getLat_lng());

		return this;
	}

	/**
	 * Overwrite the set of allowed TravelModes.
	 * 
	 * @param modes
	 * @return
	 */
	public RoutingRequestBuilder setTravelModes(TravelModeSet modes) {
		routingRequest.setModes(modes.toTraverseModeSet());
		return this;
	}

	/**
	 * Set the trip origin.
	 * 
	 * @param from
	 * @return self reference
	 */
	public RoutingRequestBuilder setOrigin(Location origin) {
		routingRequest.setFrom(latLngToString(origin.getLat_lng()));
		return this;
	}

	/**
	 * Set the trip origin.
	 * 
	 * @param from
	 * @return self reference
	 */
	public RoutingRequestBuilder setOrigin(LatLng origin) {
		routingRequest.setFrom(latLngToString(origin));
		return this;
	}

	/**
	 * Set the trip destination.
	 * 
	 * @param from
	 * @return self reference
	 */
	public RoutingRequestBuilder setDestination(Location dest) {
		routingRequest.setTo(latLngToString(dest.getLat_lng()));
		return this;
	}

	/**
	 * Set the trip destination.
	 * 
	 * @param from
	 * @return self reference
	 */
	public RoutingRequestBuilder setDestination(LatLng dest) {
		routingRequest.setTo(latLngToString(dest));
		return this;
	}

	/**
	 * Set the graph to route on.
	 * 
	 * @param g
	 * @return self reference.
	 */
	public RoutingRequestBuilder setGraph(Graph g) {
		graph = g;
		return this;
	}

	/**
	 * Set the number of itineraries to return.
	 * 
	 * @param n
	 * @return self reference.
	 */
	public RoutingRequestBuilder setNumItineraries(int n) {
		routingRequest.setNumItineraries(n);
		return this;
	}

	/**
	 * Build a RoutingRequest from the accumulated parameters.
	 * 
	 * @return
	 */
	public RoutingRequest build() {
		// Set the graph at the end to avoid certain complications.
		if (graph != null) {
			LOG.warn("Graph is null. This better be a test.");
			routingRequest.setRoutingContext(graph);
		}
		return routingRequest;
	}
}