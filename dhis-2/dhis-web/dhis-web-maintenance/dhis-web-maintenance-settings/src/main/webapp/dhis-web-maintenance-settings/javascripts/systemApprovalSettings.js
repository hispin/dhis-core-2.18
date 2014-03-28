function removeApprovalLevel( context ) {
    removeItem( context.id, context.name, i18n_confirm_delete_data_approval_level, 'removeApprovalLevel.action' );
}

function moveApprovalLevelUp( context ) {
    location.href = 'moveApprovalLevelUp.action?level=' + context.id;
}

function moveApprovalLevelDown( context ) {
    location.href = 'moveApprovalLevelDown.action?level=' + context.id;
}
