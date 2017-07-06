/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.redpen.internal.Job;


import org.xwiki.job.DefaultJobStatus;
import org.xwiki.job.event.status.JobStatus;
import org.xwiki.logging.LoggerManager;
import org.xwiki.observation.ObservationManager;

/**
 * Contains Job status of CheckerJob.
 * @version $Id: $
 * @since 1.0
 */

public class CheckerJobStatus extends DefaultJobStatus<CheckerRequest>
{
    private boolean canceled;

    /**
     * @param request Request for Content Checker to be run
     * @param observationManager observationManager
     * @param loggerManager logger manager
     * @param parentJobStatus parent job
     */
    public CheckerJobStatus(CheckerRequest request, ObservationManager observationManager,
            LoggerManager loggerManager, JobStatus parentJobStatus)
    {
        super(request, parentJobStatus, observationManager, loggerManager);
    }

    /**
     * Cancels the job.
     */
    public void cancel()
    {
        this.canceled = true;
    }

    /**
     * @return true if canceled
     */
    public boolean isCanceled()
    {
        return this.canceled;
    }
}
