/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.acme.vehiclerouting.domain.timewindowed.solver;

import java.util.Objects;

import org.acme.vehiclerouting.domain.Ride;
import org.acme.vehiclerouting.domain.Standstill;
import org.acme.vehiclerouting.domain.Vehicle;
import org.acme.vehiclerouting.domain.VehicleRoutingSolution;
import org.acme.vehiclerouting.domain.timewindowed.TimeWindowedRide;
import org.acme.vehiclerouting.domain.timewindowed.TimeWindowedDepot;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

// TODO When this class is added only for TimeWindowedRide, use TimeWindowedRide instead of Ride
public class ArrivalTimeUpdatingVariableListener implements VariableListener<VehicleRoutingSolution, Ride> {

    @Override
    public void beforeEntityAdded(ScoreDirector<VehicleRoutingSolution> scoreDirector, Ride ride) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector<VehicleRoutingSolution> scoreDirector, Ride ride) {
        if (ride instanceof TimeWindowedRide) {
            updateArrivalTime(scoreDirector, (TimeWindowedRide) ride);
        }
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<VehicleRoutingSolution> scoreDirector, Ride ride) {
        // Do nothing
    }

    @Override
    public void afterVariableChanged(ScoreDirector<VehicleRoutingSolution> scoreDirector, Ride ride) {
        if (ride instanceof TimeWindowedRide) {
            updateArrivalTime(scoreDirector, (TimeWindowedRide) ride);
        }
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<VehicleRoutingSolution> scoreDirector, Ride ride) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<VehicleRoutingSolution> scoreDirector, Ride ride) {
        // Do nothing
    }

    protected void updateArrivalTime(ScoreDirector<VehicleRoutingSolution> scoreDirector,
            TimeWindowedRide sourceRide) {
        Standstill previousStandstill = sourceRide.getPreviousStandstill();
        Long departureTime = previousStandstill == null ? null
                : (previousStandstill instanceof TimeWindowedRide)
                        ? ((TimeWindowedRide) previousStandstill).getDepartureTime()
                        : ((TimeWindowedDepot) ((Vehicle) previousStandstill).getDepot()).getReadyTime();
        TimeWindowedRide shadowRide = sourceRide;
        Long arrivalTime = calculateArrivalTime(shadowRide, departureTime);
        while (shadowRide != null && !Objects.equals(shadowRide.getArrivalTime(), arrivalTime)) {
            scoreDirector.beforeVariableChanged(shadowRide, "arrivalTime");
            shadowRide.setArrivalTime(arrivalTime);
            scoreDirector.afterVariableChanged(shadowRide, "arrivalTime");
            departureTime = shadowRide.getDepartureTime();
            shadowRide = shadowRide.getNextRide();
            arrivalTime = calculateArrivalTime(shadowRide, departureTime);
        }
    }

    private Long calculateArrivalTime(TimeWindowedRide ride, Long previousDepartureTime) {
        if (ride == null || ride.getPreviousStandstill() == null) {
            return null;
        }
        if (ride.getPreviousStandstill() instanceof Vehicle) {
            // PreviousStandstill is the Vehicle, so we leave from the Depot at the best
            // suitable time
            return Math.max(ride.getReadyTime(),
                    previousDepartureTime + ride.getDistanceFromPreviousStandstill());
        }
        return previousDepartureTime + ride.getDistanceFromPreviousStandstill();
    }

}
