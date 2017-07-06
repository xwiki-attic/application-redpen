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
package org.xwiki.contrib.redpen.script;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.bridge.DocumentAccessBridge;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.redpen.internal.Job.CheckerJobStatus;
import org.xwiki.contrib.redpen.internal.Job.CheckerRequest;
import org.xwiki.job.JobExecutor;
import org.xwiki.job.JobStatusStore;
import org.xwiki.script.service.ScriptService;

/**
 * Script Service allowing checker job to be initiated on client side.
 *
 * @version $Id: $
 * @since 1.0
 */
@Component
@Named(CheckerJobScriptService.ROLE_HINT)
@Singleton
public class CheckerJobScriptService implements ScriptService
{
    /**
     * Role hint of this script service.
     */
    public static final String ROLE_HINT = "redpenJobScriptService";

    /**
     * ID/Role hint for the checker job.
     */
    public static final String CONTENT_CHECKER = "content_checker";

    @Inject
    private JobExecutor jobExecutor;

    @Inject
    private JobStatusStore jobStatusStore;

    @Inject
    private DocumentAccessBridge documentAccessBridge;

    /**
     * @param spaceList List of spaces to run checks on, in format of string, delimited by ","
     * @return the id of the job
     */
    public String runCheck(String spaceList)
    {

        CheckerRequest checkerRequest = createCheckerRequest(spaceList);

        try {
            this.jobExecutor.execute(CONTENT_CHECKER, checkerRequest);

            List<String> checkerId = checkerRequest.getId();
            return checkerId.get(checkerId.size() - 1);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param checkerJobId id of this job
     * @return the job status
     */
    public CheckerJobStatus getCheckerStatus(String checkerJobId)
    {
        return (CheckerJobStatus) this.jobStatusStore.getJobStatus(getJobId(checkerJobId));
    }

    private CheckerRequest createCheckerRequest(String spaceList)
    {
        CheckerRequest checkerRequest = new CheckerRequest();
        checkerRequest.setSpaceList(spaceList);
        checkerRequest.setId(getNewJobId());
        checkerRequest.setCheckRights(true);
        checkerRequest.setUserReference(this.documentAccessBridge.getCurrentUserReference());

        return checkerRequest;
    }

    private List<String> getNewJobId()
    {
        return getJobId(UUID.randomUUID().toString());
    }

    private List<String> getJobId(String suffix)
    {
        return Arrays.asList(ROLE_HINT, CONTENT_CHECKER, suffix);
    }
}
