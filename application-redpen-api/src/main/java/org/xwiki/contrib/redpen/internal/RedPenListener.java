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

package org.xwiki.contrib.redpen.internal;

/**
 * Created by DeSheng on 14/6/2017.
 */

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.bridge.event.DocumentCreatingEvent;
import org.xwiki.bridge.event.DocumentUpdatingEvent;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.redpen.ContentValidator;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.event.CancelableEvent;
import org.xwiki.observation.event.Event;

import com.xpn.xwiki.doc.XWikiDocument;


/**
 * This component takes in a Wiki page's content whenever user saves the page,
 * and appends the validation results at the end of the document for reference.
 * @version $Id: $
 * @since 1.0
 */

@Component
@Named("RedpenListener")
@Singleton
public class RedPenListener implements EventListener
{
    @Inject
    private Logger logger;

    @Inject
    @Named("redpen-validator")
    private ContentValidator proofreader;

    @Inject
    @Named("Syntaxconverter")
    private RedPenValidationConfiguration redpenconfig;

    @Override public String getName()
    {
        return "RedpenListener";
    }

    @Override public List<Event> getEvents()
    {

        return Arrays.<Event>asList(new DocumentCreatingEvent(), new DocumentUpdatingEvent());
    }


    @Override public void onEvent(Event event, Object source, Object data)

    {
        if (this.redpenconfig.willStart()) {
            String confirmationText = "Document validated";
            this.logger.info("Starting validating procedure");
            XWikiDocument document = (XWikiDocument) source;
            String textObject = document.getContent();

            String validationResult = this.proofreader.validate(textObject);

            document.setContent(textObject + validationResult);
            if (event instanceof CancelableEvent) {
                ((CancelableEvent) event).cancel(confirmationText);
            }
        }
    }

}
