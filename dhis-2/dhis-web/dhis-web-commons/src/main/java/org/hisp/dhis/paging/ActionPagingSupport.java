package org.hisp.dhis.paging;

import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.system.paging.Paging;

import com.opensymphony.xwork2.ActionSupport;


/**
 * @author Quang Nguyen
 * @version Jul 11, 2010 10:01:40 PM
 */
public abstract class ActionPagingSupport extends ActionSupport
{
    protected static final Integer DEFAULT_PAGE_SIZE = 50;
    
    protected Integer currentPage;
    
    public void setCurrentPage( Integer currentPage )
    {
        this.currentPage = currentPage;
    }

    protected Integer pageSize;
    
    public void setPageSize( Integer pageSize )
    {
        this.pageSize = pageSize;
    }
    
    protected Paging paging;
    
    public Paging getPaging()
    {
        return paging;
    }

    private String getCurrentLink()
    {
        return ServletActionContext.getRequest().getRequestURI();
    }

    protected Paging createPaging( Integer totalRecord )
    {
        Paging resultPaging = new Paging( getCurrentLink(), pageSize == null ? DEFAULT_PAGE_SIZE : pageSize );
        
        resultPaging.setCurrentPage( currentPage == null ? 0 : currentPage );
           
        resultPaging.setTotal( totalRecord );
        
        return resultPaging;
    }
    
    protected List getBlockElement( List elementList, int startPos, int pageSize )
    {
        List returnList;

        try
        {
            returnList = elementList.subList( startPos, startPos + pageSize );
        }
        catch ( IndexOutOfBoundsException ex )
        {
            returnList = elementList.subList( startPos, elementList.size() );
        }

        return returnList;
    }
}
