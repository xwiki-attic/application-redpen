package org.xwiki.contrib.redpen.test.po;

import java.util.Arrays;

import org.xwiki.model.EntityType;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.WikiReference;
import org.xwiki.test.ui.po.ViewPage;

public class RedPenHomePage extends ViewPage
{
    /**
     * Main wiki id.
     */
    public static final String MAIN_WIKI = "xwiki";

    /**
     * FAQ home page document reference.
     */
    public static final RedPenHomePage DEFAULT_FAQ_HOME_PAGE = new RedPenHomePage(
            new DocumentReference(MAIN_WIKI, Arrays.asList("Content Checker"), "WebHome"));

    private EntityReference homeReference;


    /**
     * @param homeReference the reference to the home page where the Content Checker app is installed
     */
    public RedPenHomePage(EntityReference homeReference)
    {
        this.homeReference = homeReference;
    }

    /**
     * Opens the home page.
     */
    public void gotoPage()
    {
        getUtil().gotoPage(this.homeReference);
    }

    /**
     * @return the String reference to the space where the Content Checker app is installed
     */
    public String getSpaces()
    {
        return getUtil().serializeReference(
                this.homeReference.extractReference(EntityType.SPACE).removeParent(new WikiReference(MAIN_WIKI)));
    }

    /**
     * @return the name of the home page where the FAQ app is installed (e.g. "{@code WebHome})
     */
    public String getPage()
    {
        return this.homeReference.getName();
    }


}