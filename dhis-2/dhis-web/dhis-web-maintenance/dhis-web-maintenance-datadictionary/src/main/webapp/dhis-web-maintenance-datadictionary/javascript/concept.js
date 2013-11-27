// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

/**
 * Tried to find a function with fnName in window scope.
 *
 * TODO: extend to search for more scopes
 *
 * @param fnName Name of function to search for
 * @returns Function
 */
function findFunction( fnName ) {
  if( typeof window[fnName] !== 'function' ) {
    throw new Error('target-fnName \'' + fnName + '\' does not point to a valid function.')
  }

  return window[fnName];
}

$(function() {
  var $list = $('#list');
  var $contextMenu = $('.contextMenu');
  var $contextMenuItems = $('.contextMenuItems');

  $contextMenuItems.on('touchend click', 'li', function(e) {
    var context = {
      'id': $contextMenu.data('id'),
      'uid': $contextMenu.data('uid'),
      'name': $contextMenu.data('name')
    };

    var $target = $(e.target);
    var targetFn = $target.data('target-fn');
    var fn = findFunction(targetFn);

    $contextMenu.hide();
    fn(context);
  });

  $list.on('click', 'td', function( e ) {
    $contextMenu.show();
    $contextMenu.css({left: e.pageX, top: e.pageY});

    var $target = $(e.target);
    $contextMenu.data('id', $target.data('id'));
    $contextMenu.data('uid', $target.data('uid'));
    $contextMenu.data('name', $target.data('name'));

    return false;
  });

  $(document).on('touchend click', function( e ) {
    if( $contextMenu.is(":visible") ) {
      $contextMenu.hide();
    }

    $contextMenu.removeData('id');
  });
});

// -----------------------------------------------------------------------------
// Context Menu Actions
// -----------------------------------------------------------------------------

function logContext( context ) {
  console.log('context: ', context);
}

function showUpdateConcept( context ) {
  location.href = 'showUpdateConceptForm.action?id=' + context.id;
}

function showConceptDetails( context ) {
  $.post('getConcept.action', { id: context.id }, function( json ) {
    setInnerHTML('nameField', json.concept.name);
    showDetails();
  });
}

function removeConcept( context ) {
  removeItem(context.id, context.name, i18n_confirm_delete, 'removeConcept.action');
}
