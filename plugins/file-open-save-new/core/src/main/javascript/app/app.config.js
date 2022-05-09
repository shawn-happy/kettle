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
 * Defines the config for the file open save dialog
 */
define([], function () {
  'use strict';

  config.$inject = ['$stateProvider'];

  /**
   * The config for the file open save app
   *
   * @param {Object} $stateProvider - Controls the state of the app
   */
  function config($stateProvider) {

    $stateProvider
        .state('save', {
          url: "/save",
          template: "<fileOpenSaveApp></fileOpenSaveApp>"
        })
        .state('saveTo', {
          url: "/saveTo",
          template: "<fileOpenSaveApp></fileOpenSaveApp>"
        })
        .state('saveToFileFolder', {
          url: "/saveToFileFolder",
          template: "<fileOpenSaveApp></fileOpenSaveApp>"
        })
        .state('open', {
          url: "/open",
          template: "<fileOpenSaveApp></fileOpenSaveApp>"
        })
        .state('selectFolder', {
          url: "/selectFolder",
          template: "<fileOpenSaveApp></fileOpenSaveApp>"
        })
        .state('selectFile', {
          url: "/selectFile",
          template: "<fileOpenSaveApp></fileOpenSaveApp>"
        })
        .state('selectFileFolder', {
          url: "/selectFileFolder",
          template: "<fileOpenSaveApp></fileOpenSaveApp>"
        });
  }

  return config;
});
