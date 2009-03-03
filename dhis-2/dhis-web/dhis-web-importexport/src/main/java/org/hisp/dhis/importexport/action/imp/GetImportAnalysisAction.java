package org.hisp.dhis.importexport.action.imp;

import static org.hisp.dhis.util.InternalProcessUtil.PROCESS_KEY_IMPORT;
import static org.hisp.dhis.importexport.action.util.ImportExportInternalProcessUtil.*;

import org.amplecode.cave.process.ProcessCoordinator;
import org.amplecode.cave.process.ProcessExecutor;
import org.hisp.dhis.importexport.analysis.ImportAnalysis;
import org.hisp.dhis.system.process.OutputHolderState;

import com.opensymphony.xwork.Action;

public class GetImportAnalysisAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProcessCoordinator processCoordinator;

    public void setProcessCoordinator( ProcessCoordinator processCoordinator )
    {
        this.processCoordinator = processCoordinator;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private ImportAnalysis analysis;
    
    public ImportAnalysis getAnalysis()
    {
        return analysis;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
    public String execute()
    {
        String id = getCurrentRunningProcess( PROCESS_KEY_IMPORT );
            
        ProcessExecutor executor = processCoordinator.getProcess( id );
            
        if ( executor != null && executor.getProcess() != null && executor.getState() != null )
        {
            OutputHolderState state = (OutputHolderState)executor.getState();
            
            analysis = (ImportAnalysis) state.getOutput();
            
            setCurrentRunningProcessType( TYPE_IMPORT );
        }
        
        return SUCCESS;
    }
}
