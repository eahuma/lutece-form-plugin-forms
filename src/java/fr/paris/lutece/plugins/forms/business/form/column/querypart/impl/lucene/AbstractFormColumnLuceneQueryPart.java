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
package fr.paris.lutece.plugins.forms.business.form.column.querypart.impl.lucene;

import fr.paris.lutece.plugins.forms.business.form.column.FormColumnCell;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.lucene.document.Document;

/**
 * Abstract class for FormColumnQueryPart
 */
public abstract class AbstractFormColumnLuceneQueryPart implements IFormColumnLuceneQueryPart
{
    /**
     * Get a map of values fetched from Lucene document
     * @param document
     * @return a Map of values feteched form Lucene document
     */
    protected abstract Map<String, Object> getMapFormColumnValues( Document document );
    
    // Variables
    private IFormColumn _formColumn;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFormColumn( IFormColumn formColumn )
    {
        _formColumn = formColumn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IFormColumn getFormColumn( )
    {
        return _formColumn;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public FormColumnCell getFormColumnCell( Document document )
    {
        Map<String, Object> mapFormColumnValues = new LinkedHashMap<>( );

        IFormColumn formColumn = getFormColumn( );
        if ( formColumn != null )
        {
            mapFormColumnValues = getMapFormColumnValues( document );
        }

        FormColumnCell formColumnCell = new FormColumnCell( );
        formColumnCell.setFormColumnCellValues( mapFormColumnValues );

        return formColumnCell;
    }
}
