/*!
 * Copyright 2019 Hitachi Vantara. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * The File Open and Save Error component.
 *
 * This provides the component for the file open and save Error Message dialogs.
 *
 * @module components/error/error.component
 * @property {String} name The name of the Angular component.
 * @property {Object} options The JSON object containing the configurations for this component.
 */
define([
  "text!./error.html",
  "../utils",
  "pentaho/i18n-osgi!file-open-save-new.messages",
  "css!./error.css"
], function(errorTemplate, utils, i18n) {
  "use strict";

  var options = {
    bindings: {
      errorType: "<",
      errorFiles: "<",
      errorFolder: "<",
      onErrorConfirm: "&",
      onErrorCancel: "&"
    },
    template: errorTemplate,
    controllerAs: "vm",
    controller: errorController
  };

  errorController.$inject = ["$scope", "$timeout"];

  /**
   * The Error Controller. This provides the controller for the error component.
   * @param {Object} $scope - Application model
   * @param {Object} $timeout - Angular wrapper around window.setTimeout
   */
  function errorController($scope, $timeout) {
    var _buffer = 25;
    var _max = 1440 - _buffer; // maximum width of 4 lines at 360px each minus a small buffer
    var _ellipsis = "...";
    var _maxHeight = 73; // is actually 72, but IE adds an extra px for some reason.
    var vm = this;
    vm.$onInit = onInit;
    vm.$onChanges = onChanges;
    vm.errorCancel = errorCancel;
    vm.errorConfirm = errorConfirm;
    vm.hideConfirmButton = hideConfirmButton;

    /**
     * The $onInit hook of components lifecycle which is called on each controller
     * after all the controllers on an element have been constructed and had their
     * bindings initialized. We use this hook to put initialization code for our controller.
     */
    function onInit() {
      vm.breakAll = false;
    }

    /**
     * Called whenever one-way bindings are updated.
     */
    function onChanges() {
      if (vm.errorType !== 0) {
        _setMessages();
      }
    }

    /**
     * Called when user clicks the Cancel button on error dialog.
     * Then calls parent component (app) to handle cancelling of error.
     */
    function errorCancel() {
      vm.onErrorCancel();
    }

    /**
     * Called when user clicks the Yes or Confirm button on error dialog.
     * Then calls parent component (app) to handle confirmation of error.
     */
    function errorConfirm() {
      vm.onErrorConfirm();
    }

    /**
     * Check the errorType and returns true if it is not 1, 5, or 6. Returns false otherwise.
     * @return {boolean} - true if the errorType is not 1, 5, or 6. Returns false otherwise.
     */
    function hideConfirmButton() {
      return vm.errorType !== 1 && vm.errorType !== 5 && vm.errorType !== 6 && vm.errorType !== 21 && vm.errorType !== 22;
    }

    /**
     * Calls the setMessage function according to the errorType.
     * errorType:
     * 1. Overwrite Trans/Job
     * 2. Folder Exists
     * 3. Unable to Save
     * 4. Unable to create folder
     * 5. Delete file confirmation
     * 6. Delete folder confirmation
     * 7. File Exists
     * 8. Unable to delete folder
     * 9. Unable to delete file
     * 10. Unable to rename folder
     * 11. Unable to rename file
     * 12. Unable to rename file b/c already opened in spoon
     * 13. Unable to delete folder b/c in use
     * 14. Unable to delete file b/c in use
     * 15. Unable to rename folder b/c it has an open file in it or a subfolder of it
     * 16. Unable to open recent file
     * 17. Save With Invalid File Name
     * 18. Create With Invalid File Name
     * 19. Rename With Invalid File Name
     * 20. Cannot move file to directory
     * 21. Warn about deleting multiple files
     * 22. Warn Overwrite File/Folder
     *
     * @private
     */
    function _setMessages() {
      vm.breakAll = false;
      switch (vm.errorType) {
        case 1:// Overwrite
          var msg = vm.errorFiles[0].type === "job" ?
            i18n.get("file-open-save-plugin.error.overwrite.job.top-before.message") + " " :
            i18n.get("file-open-save-plugin.error.overwrite.trans.top-before.message") + " ";
          _handleOverwriteMessage(msg);
          vm.breakAll = true;
          break;
        case 2:// Folder Exists
          _setMessage(i18n.get("file-open-save-plugin.error.folder-exists.title"),
            i18n.get("file-open-save-plugin.error.folder-exists.top.message") + " ",
            vm.errorFolder.newName + ".",
            "",
            i18n.get("file-open-save-plugin.error.folder-exists.bottom.message"),
            "",
            i18n.get("file-open-save-plugin.error.folder-exists.close.button"));
          _handleLongMessages(".");
          break;
        case 3:// Unable to Save
          _setMessage(i18n.get("file-open-save-plugin.error.unable-to-save.title"),
            i18n.get("file-open-save-plugin.error.unable-to-save.message"),
            "", "", "", "",
            i18n.get("file-open-save-plugin.error.unable-to-save.close.button"));
          break;
        case 4:// Unable to create folder
          _setMessage(i18n.get("file-open-save-plugin.error.unable-to-create-folder.title"),
            i18n.get("file-open-save-plugin.error.unable-to-create-folder.message"),
            "", "", "", "",
            i18n.get("file-open-save-plugin.error.unable-to-create-folder.close.button"));
          break;
        case 5:// Delete file
          var deleteFileBefore = i18n.get("file-open-save-plugin.error.delete-file.message") + " ";
          var deleteFileName = vm.errorFiles[0].name;
          var deleteFileFilenameMaxWidth = _max - utils.getTextWidth(deleteFileBefore + " ?" + _ellipsis);
          if (utils.getTextWidth(deleteFileName) > deleteFileFilenameMaxWidth) {
            deleteFileName = utils.truncateString(deleteFileName, deleteFileFilenameMaxWidth) + _ellipsis + " ";
          }
          _setMessage(i18n.get("file-open-save-plugin.error.delete-file.title"),
            deleteFileBefore,
            deleteFileName + "?",
            "", "",
            i18n.get("file-open-save-plugin.error.delete-file.accept.button"),
            i18n.get("file-open-save-plugin.error.delete-file.no.button"));
          vm.breakAll = true;
          break;
        case 6:// Delete folder
          var deleteFolderBefore = i18n.get("file-open-save-plugin.error.delete-folder.before.message") + " ";
          var deleteFolderName = vm.errorFolder.name;
          var deleteFolderAfter = " " + i18n.get("file-open-save-plugin.error.delete-folder.after.message");
          var deleteFolderFoldernameMaxWidth = _max -
            utils.getTextWidth(deleteFolderBefore + deleteFolderAfter + _ellipsis);
          if (utils.getTextWidth(deleteFolderName) > deleteFolderFoldernameMaxWidth) {
            deleteFolderName = utils.truncateString(deleteFolderName, deleteFolderFoldernameMaxWidth) + _ellipsis;
          }
          _setMessage(i18n.get("file-open-save-plugin.error.delete-folder.title"),
            deleteFolderBefore,
            deleteFolderName,
            deleteFolderAfter,
            "",
            i18n.get("file-open-save-plugin.error.delete-folder.accept.button"),
            i18n.get("file-open-save-plugin.error.delete-folder.no.button"));
          vm.breakAll = true;
          break;
        case 7:// File Exists
          var fileExistsBefore = i18n.get("file-open-save-plugin.error.file-exists.top.message") + " ";
          var fileExistsFilenameMaxWidth = _max - utils.getTextWidth(fileExistsBefore + " ." + _ellipsis);
          var fileExistsFilename = vm.errorFiles[0].newName;
          if (utils.getTextWidth(fileExistsFilename) > fileExistsFilenameMaxWidth) {
            fileExistsFilename = utils.truncateString(fileExistsFilename, fileExistsFilenameMaxWidth) + _ellipsis + " ";
          }
          _setMessage(i18n.get("file-open-save-plugin.error.file-exists.title"),
            fileExistsBefore,
            fileExistsFilename + ".",
            "",
            i18n.get("file-open-save-plugin.error.file-exists.bottom.message"),
            "",
            i18n.get("file-open-save-plugin.error.file-exists.close.button"));
          vm.breakAll = true;
          break;
        case 8:// Unable to delete folder
          _setMessage(i18n.get("file-open-save-plugin.error.unable-to-delete-folder.title"),
              i18n.get("file-open-save-plugin.error.unable-to-delete-folder.message"),
              "", "", "", "",
              i18n.get("file-open-save-plugin.error.unable-to-delete-folder.close.button"));
          break;
        case 9:// Unable to delete file
          _setMessage(i18n.get("file-open-save-plugin.error.unable-to-delete-file.title"),
              i18n.get("file-open-save-plugin.error.unable-to-delete-file.message"),
              "", "", "", "",
              i18n.get("file-open-save-plugin.error.unable-to-delete-file.close.button"));
          break;
        case 10:// Unable to rename folder
          _setMessage(i18n.get("file-open-save-plugin.error.unable-to-rename-folder.title"),
            i18n.get("file-open-save-plugin.error.unable-to-rename-folder.message"),
            "", "", "", "",
            i18n.get("file-open-save-plugin.error.unable-to-rename-folder.close.button"));
          break;
        case 11:// Unable to rename file
          _setMessage(i18n.get("file-open-save-plugin.error.unable-to-rename-file.title"),
            i18n.get("file-open-save-plugin.error.unable-to-rename-file.message"),
            "", "", "", "",
            i18n.get("file-open-save-plugin.error.unable-to-rename-file.close.button"));
          break;
        case 12:// Unable to rename file b/c already opened in spoon
          _setMessage(i18n.get("file-open-save-plugin.error.unable-to-rename-file-opened.title"),
            i18n.get("file-open-save-plugin.error.unable-to-rename-file-opened.top.message"),
            "", "",
            i18n.get("file-open-save-plugin.error.unable-to-rename-file-opened.bottom.message"),
            "",
            i18n.get("file-open-save-plugin.error.unable-to-rename-file-opened.close.button"));
          break;
        case 13:// Unable to delete folder b/c in use
          _setMessage(i18n.get("file-open-save-plugin.error.unable-to-delete-folder-opened.title"),
            i18n.get("file-open-save-plugin.error.unable-to-delete-folder-opened.top.message"),
            "", "",
            i18n.get("file-open-save-plugin.error.unable-to-delete-folder-opened.bottom.message"),
            "",
            i18n.get("file-open-save-plugin.error.unable-to-delete-folder-opened.close.button"));
          break;
        case 14:// Unable to delete file b/c in use
          _setMessage(i18n.get("file-open-save-plugin.error.unable-to-delete-file-opened.title"),
            i18n.get("file-open-save-plugin.error.unable-to-delete-file-opened.top.message"),
            "", "",
            i18n.get("file-open-save-plugin.error.unable-to-delete-file-opened.bottom.message"),
            "",
            i18n.get("file-open-save-plugin.error.unable-to-delete-file-opened.close.button"));
          break;
        case 15:// Unable to rename folder b/c it has an open file in it or a subfolder of it
          _setMessage(i18n.get("file-open-save-plugin.error.unable-to-rename-folder-opened.title"),
            i18n.get("file-open-save-plugin.error.unable-to-rename-folder-opened.top.message"),
            "", "",
            i18n.get("file-open-save-plugin.error.unable-to-rename-folder-opened.bottom.message"),
            "",
            i18n.get("file-open-save-plugin.error.unable-to-rename-folder-opened.close.button"));
          break;
        case 16:// Unable to Open Recent file
          // file type values are known, and therefore we do not need to calculate the message width - we know it
          // will fit within the limit
          var fullMessage = i18n.get("file-open-save-plugin.missing-recent.message", {"filetype": vm.errorFiles[0].type});
          _setMessage(i18n.get("file-open-save-plugin.missing-recent.title"),
              fullMessage,
              "", "", "", "",
              i18n.get("file-open-save-plugin.missing-recent.close.button"));
          break;
        case 17:// Save With Invalid File Name
          _setMessage(i18n.get("file-open-save-plugin.error.invalid-file-name.save.title"),
              i18n.get("file-open-save-plugin.error.invalid-file-name.save.top.message"),
              "", "", "", "",
              i18n.get("file-open-save-plugin.error.invalid-file-name.save.ok.button"));
          break;
        case 18:// Create With Invalid File Name
          _setMessage(i18n.get("file-open-save-plugin.error.invalid-file-name.create.title"),
              i18n.get("file-open-save-plugin.error.invalid-file-name.create.top.message"),
              "", "", "", "",
              i18n.get("file-open-save-plugin.error.invalid-file-name.create.ok.button"));
          break;
        case 19:// Rename With Invalid File Name
          _setMessage(i18n.get("file-open-save-plugin.error.invalid-file-name.rename.title"),
              i18n.get("file-open-save-plugin.error.invalid-file-name.rename.top.message"),
              "", "", "", "",
              i18n.get("file-open-save-plugin.error.invalid-file-name.rename.ok.button"));
          break;
        case 20:// Cannot move file to directory
          _setMessage(i18n.get("file-open-save-plugin.error.unable-to-move-file.title"),
              i18n.get("file-open-save-plugin.error.unable-to-move-file.message"),
              "", "", "", "",
              i18n.get("file-open-save-plugin.error.unable-to-move-file.close.button"));
          break;
        case 21:// Warn about deleting multiple files
          var deleteManyBefore = i18n.get("file-open-save-plugin.error.delete-many.before.message") + " ";
          var deleteManyName = vm.errorFiles.length;
          var deleteManyAfter = " " + i18n.get("file-open-save-plugin.error.delete-many.after.message");
          var deleteManyMaxWidth = _max -
              utils.getTextWidth(deleteManyBefore + deleteManyAfter + _ellipsis);
          if (utils.getTextWidth(deleteManyName) > deleteManyMaxWidth) {
            deleteManyName = utils.truncateString(deleteManyName, deleteManyMaxWidth) + _ellipsis;
          }
          _setMessage(i18n.get("file-open-save-plugin.error.delete-many.title"),
              deleteManyBefore,
              deleteManyName,
              deleteManyAfter,
              "",
              i18n.get("file-open-save-plugin.error.delete-many.accept.button"),
              i18n.get("file-open-save-plugin.error.delete-many.no.button"));
          vm.breakAll = true;
          break;
        case 22: // Warn Overwrite File/Folder
          var msg = vm.errorFiles[0].type === "folder" ?
              i18n.get("file-open-save-plugin.error.overwrite.folder.top-before.message") + " " :
              i18n.get("file-open-save-plugin.error.overwrite.file.top-before.message") + " ";
          _handleOverwriteMessage(msg);
          vm.breakAll = true;
          break;
        default:
          _setMessage("", "", "", "", "", "", "");
          break;
      }
    }

    /**
     * @param {String} title - Title of Error Dialog
     * @param {String} topBefore - Beginning part of top message
     * @param {String} topMiddle - Middle part of top message
     * @param {String} topAfter - Ending part of top message
     * @param {String} bottom - Bottom message
     * @param {String} confirm - Confirmation button text
     * @param {String} cancel - Cancel button text
     * @private
     */
    function _setMessage(title, topBefore, topMiddle, topAfter, bottom, confirm, cancel) {
      vm.errorTitle = title;
      vm.errorMessageTopBefore = topBefore + topMiddle;
      vm.errorMessageTopAfter = topAfter;
      vm.errorMessageBottom = bottom;
      vm.errorConfirmButton = confirm;
      vm.errorCancelButton = cancel;
    }

    /**
     * Helper method for overwrite error types.
     * @param {String} msg - Message to include in overwrite dialog.
     * @private
     */
    function _handleOverwriteMessage(msg) {

      var overwriteAfter = " " + i18n.get("file-open-save-plugin.error.overwrite.top-after.message");
      var overwriteFilenameMaxWidth = _max - utils.getTextWidth(msg + overwriteAfter + _ellipsis);
      var overwriteFilename = vm.errorFiles[0].name;
      if (utils.getTextWidth(overwriteFilename) > overwriteFilenameMaxWidth) {
        overwriteFilename = utils.truncateString(overwriteFilename, overwriteFilenameMaxWidth) + _ellipsis;
      }
      _setMessage(i18n.get("file-open-save-plugin.error.overwrite.title"),
          msg,
          overwriteFilename,
          overwriteAfter,
          i18n.get("file-open-save-plugin.error.overwrite.bottom.message"),
          i18n.get("file-open-save-plugin.error.overwrite.accept.button"),
          i18n.get("file-open-save-plugin.error.overwrite.cancel.button"));

    }

    /**
     * Handles long messages and truncates them (adding ellipsis and the ending).
     * Is message height more than max? If so, break all words and recheck, then truncate accordingly.
     * @param {String} ending - Ending punctuation of message
     * @private
     */
    function _handleLongMessages(ending) {
      var errorMiddle = document.getElementById("errorMiddle");
      $timeout(function() {
        if (errorMiddle.scrollHeight > _maxHeight) {
          vm.breakAll = true;
          $scope.$digest();
          if (errorMiddle.scrollHeight > _maxHeight) {
            while (errorMiddle.scrollHeight > _maxHeight) {
              vm.errorMessageTopBefore = vm.errorMessageTopBefore.substring(0, vm.errorMessageTopBefore.length - 1);
              $scope.$digest();
            }
            vm.errorMessageTopBefore = vm.errorMessageTopBefore
                .substring(0, vm.errorMessageTopBefore.length - 5) + _ellipsis + ending;
          }
        }
      });
    }
  }

  return {
    name: "error",
    options: options
  };
});
