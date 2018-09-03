/*
 * Copyright (c) 2002-2018, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.forms.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDisplayService;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * 
 * Implementation of ICompositeDisplay for Question
 *
 */
public class CompositeQuestionDisplay implements ICompositeDisplay
{
    // Templates
    private static final String TEMPLATE_QUESTION_EDITION_FRONTOFFICE = "/skin/plugins/forms/composite_template/view_question.html";
    private static final String TEMPLATE_QUESTION_READONLY_BACKOFFICE = "/admin/plugins/forms/composite/view_question.html";
    private static final String TEMPLATE_QUESTION_EDITION_BACKOFFICE = TEMPLATE_QUESTION_READONLY_BACKOFFICE;

    // Marks
    private static final String MARK_QUESTION_ENTRY = "questionEntry";
    private static final String MARK_ENTRY_ITERATION_NUMBER = "entry_iteration_number";

    private Question _question;
    private FormDisplay _formDisplay;
    private String _strIconName;

    /**
     * Constructor
     * 
     * @param formDisplay
     *            the form display
     * @param formResponse
     *            the form response
     * @param nIterationNumber
     *            the iteration number
     */
    public CompositeQuestionDisplay( FormDisplay formDisplay, FormResponse formResponse, int nIterationNumber )
    {
        _formDisplay = formDisplay;

        initComposite( formResponse, nIterationNumber );
    }

    /**
     * Initializes the composite
     * 
     * @param formResponse
     *            the form response
     * @param nIterationNumber
     *            the iteration number
     */
    private void initComposite( FormResponse formResponse, int nIterationNumber )
    {
        _question = QuestionHome.findByPrimaryKey( _formDisplay.getCompositeId( ) );

        if ( _question.getEntry( ) != null && _question.getEntry( ).getEntryType( ) != null )
        {
            _strIconName = _question.getEntry( ).getEntryType( ).getIconName( );
        }

        _question.setIterationNumber( nIterationNumber );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCompositeHtml( List<FormQuestionResponse> listFormQuestionResponse, Locale locale, DisplayType displayType )
    {
        String strQuestionTemplate = StringUtils.EMPTY;

        if ( _question.getEntry( ) != null )
        {
            IEntryDisplayService displayService = EntryServiceManager.getInstance( ).getEntryDisplayService( _question.getEntry( ).getEntryType( ) );

            if ( displayService != null )
            {
                Map<String, Object> model = new HashMap<String, Object>( );
                model.put( MARK_ENTRY_ITERATION_NUMBER, _question.getIterationNumber( ) );
                model.put( FormsConstants.MARK_QUESTION_LIST_RESPONSES, findResponses( listFormQuestionResponse ) );
                model.put( MARK_QUESTION_ENTRY, _question.getEntry( ) );

                strQuestionTemplate = displayService.getEntryTemplateDisplay( _question.getEntry( ), locale, model, displayType );

                model.put( FormsConstants.MARK_QUESTION_CONTENT, strQuestionTemplate );
                model.put( FormsConstants.MARK_ID_QUESTION, _question.getId( ) );
                if ( _formDisplay.getDisplayControl( ) != null )
                {
                    model.put( FormsConstants.MARK_ID_DISPLAY, _formDisplay.getDisplayControl( ).getIdTargetFormDisplay( ) );
                }

                HtmlTemplate htmlTemplateQuestion = AppTemplateService.getTemplate( findTemplateFor( displayType ), locale, model );

                strQuestionTemplate = htmlTemplateQuestion != null ? htmlTemplateQuestion.getHtml( ) : StringUtils.EMPTY;
            }
        }

        return strQuestionTemplate;
    }

    /**
     * Finds the responses associated to this instance among the specified list of form question responses
     * 
     * @param listFormQuestionResponse
     *            the list of form question responses
     * @return the responses
     */
    private List<Response> findResponses( List<FormQuestionResponse> listFormQuestionResponse )
    {
        List<Response> listResponse = new ArrayList<>( );

        if ( listFormQuestionResponse != null )
        {
            for ( FormQuestionResponse formQuestionResponse : listFormQuestionResponse )
            {
                Question question = formQuestionResponse.getQuestion( );

                if ( _question.getId( ) == question.getId( ) && _question.getIterationNumber( ) == question.getIterationNumber( ) )
                {
                    listResponse = formQuestionResponse.getEntryResponse( );
                    break;
                }
            }
        }

        return listResponse;
    }

    /**
     * Finds the template to use for the specified display type
     * 
     * @param displayType
     *            the display type
     * @return the template
     */
    private String findTemplateFor( DisplayType displayType )
    {
        String strTemplate = StringUtils.EMPTY;

        if ( displayType == DisplayType.EDITION_FRONTOFFICE )
        {
            strTemplate = TEMPLATE_QUESTION_EDITION_FRONTOFFICE;
        }

        if ( displayType == DisplayType.EDITION_BACKOFFICE )
        {
            strTemplate = TEMPLATE_QUESTION_EDITION_BACKOFFICE;
        }

        if ( displayType == DisplayType.READONLY_BACKOFFICE )
        {
            strTemplate = TEMPLATE_QUESTION_READONLY_BACKOFFICE;
        }

        if ( displayType == DisplayType.READONLY_FRONTOFFICE )
        {
            strTemplate = TEMPLATE_QUESTION_READONLY_BACKOFFICE;
        }

        return strTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void iterate( int nIdFormDisplay )
    {

    }

    @Override
    public List<ICompositeDisplay> getCompositeList( )
    {
        List<ICompositeDisplay> listICompositeDisplay = new ArrayList<ICompositeDisplay>( );
        listICompositeDisplay.add( this );
        return listICompositeDisplay;
    }

    @Override
    public String getTitle( )
    {
        String strTitle = "";
        if ( _question != null && StringUtils.isNotEmpty( _question.getTitle( ) ) )
        {
            strTitle = _question.getTitle( );
        }
        return strTitle;
    }

    @Override
    public String getType( )
    {
        return _question != null ? CompositeDisplayType.QUESTION.getLabel( ) : StringUtils.EMPTY;
    }

    @Override
    public FormDisplay getFormDisplay( )
    {
        return _formDisplay;
    }

    @Override
    public String getIcon( )
    {
        return _strIconName;
    }

    @Override
    public List<Control> getAllDisplayControls( )
    {
        List<Control> listDisplayControls = new ArrayList<Control>( );

        if ( _formDisplay.getDisplayControl( ) != null )
        {
            listDisplayControls.add( _formDisplay.getDisplayControl( ) );
        }

        return listDisplayControls;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addQuestions( List<Question> listQuestion )
    {
        listQuestion.add( _question );
    }

}
