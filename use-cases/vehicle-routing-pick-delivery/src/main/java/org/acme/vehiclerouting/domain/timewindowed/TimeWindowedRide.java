/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.acme.vehiclerouting.domain.timewindowed;

import org.acme.vehiclerouting.domain.Ride;
import org.acme.vehiclerouting.domain.location.Location;
import org.acme.vehiclerouting.domain.timewindowed.solver.ArrivalTimeUpdatingVariableListener;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

@PlanningEntity
public class TimeWindowedRide extends Ride {

    // Times are multiplied by 1000 to avoid floating point arithmetic rounding
    // errors
    private long readyTime;
    private long dueTime;
    private long serviceDuration;

    // Shadow variable
    private Long arrivalTime;

    public TimeWindowedRide() {
    }

    public TimeWindowedRide(long id, Location location, int demand, long readyTime, long dueTime,
            long serviceDuration) {
        super(id, location, demand);
        this.readyTime = readyTime;
        this.dueTime = dueTime;
        this.serviceDuration = serviceDuration;
    }

    /**
     * @return a positive number, the time multiplied by 1000 to avoid floating
     *         point arithmetic rounding errors
     */
    public long getReadyTime() {
        return readyTime;
    }

    public void setReadyTime(long readyTime) {
        this.readyTime = readyTime;
    }

    /**
     * @return a positive number, the time multiplied by 1000 to avoid floating
     *         point arithmetic rounding errors
     */
    public long getDueTime() {
        return dueTime;
    }

    public void setDueTime(long dueTime) {
        this.dueTime = dueTime;
    }

    /**
     * @return a positive number, the time multiplied by 1000 to avoid floating
     *         point arithmetic rounding errors
     */
    public long getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceDuration(long serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    /**
     * @return a positive number, the time multiplied by 1000 to avoid floating
     *         point arithmetic rounding errors
     */
    @CustomShadowVariable(variableListenerClass = ArrivalTimeUpdatingVariableListener.class,
            // Arguable, to adhere to API specs (although this works), nextRide should
            // also be a source,
            // because this shadow must be triggered after nextRide (but there is no
            // need to be triggered by nextRide)
            sources = { @PlanningVariableReference(variableName = "previousStandstill") })
    public Long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    /**
     * @return a positive number, the time multiplied by 1000 to avoid floating
     *         point arithmetic rounding errors
     */
    public Long getDepartureTime() {
        if (arrivalTime == null) {
            return null;
        }
        return Math.max(arrivalTime, readyTime) + serviceDuration;
    }

    public boolean isArrivalBeforeReadyTime() {
        return arrivalTime != null && arrivalTime < readyTime;
    }

    public boolean isArrivalAfterDueTime() {
        return arrivalTime != null && dueTime < arrivalTime;
    }

    @Override
    public TimeWindowedRide getNextRide() {
        return (TimeWindowedRide) super.getNextRide();
    }

    /**
     * @return a positive number, the time multiplied by 1000 to avoid floating
     *         point arithmetic rounding errors
     */
    public long getTimeWindowGapTo(TimeWindowedRide other) {
        // dueTime doesn't account for serviceDuration
        long latestDepartureTime = dueTime + serviceDuration;
        long otherLatestDepartureTime = other.getDueTime() + other.getServiceDuration();
        if (latestDepartureTime < other.getReadyTime()) {
            return other.getReadyTime() - latestDepartureTime;
        }
        if (otherLatestDepartureTime < readyTime) {
            return readyTime - otherLatestDepartureTime;
        }
        return 0L;
    }

}
