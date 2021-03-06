<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sj" uri="/struts-jquery-tags" %>
<%@ taglib prefix="sjg" uri="/struts-jquery-grid-tags" %>

<div class="giane-tab-content-form-column">
  <div>
    <s:form id="simulation_form" theme="simple" cssClass="giane-form">
      <fieldset>
        <legend><s:text name="simulation.form" /></legend>
        <div class="giane-form-field-box">
          <s:hidden id="simulation_id" name="model.id" />
        </div>
        <div class="giane-form-field-box">
          <s:textfield
            id="simulation_name"
            name="model.name"
            label="%{getText('simulation.name.label')}"
            requiredLabel="true"
            requiredPosition="left"
            theme="xhtml"
          />
          <span class="giane-form-error-message" id="simulation_form_nameError"></span>
        </div>
        <div class="giane-form-field-box">
          <s:select
            id="simulation_network"
            name="model.network"
            label="%{getText('simulation.network.label')}"
            list="%{networks}"
            multiple="false"
            requiredLabel="true"
            requiredPosition="left"
            theme="xhtml"
            cssClass="select-field"
          />
          <span class="giane-form-error-message" id="simulation_form_networkError"></span>
        </div>
        <div class="giane-form-field-box">
          <s:textarea
            id="simulation_descr"
            name="model.descr"
            label="%{getText('simulation.descr.label')}"
            requiredLabel="false"
            requiredPosition="left"
            theme="xhtml"
          />
          <span class="giane-form-error-message" id="simulation_form_descrError"></span>
        </div>
        <div>
          <table class="submit-button-table">
            <tbody>
              <tr>
                <td class="two-buttons-first-cell">
                  <sj:submit
                    value="%{getText('form.createButton.label')}"
                    button="true"
                    cssClass="giane-form-button"
                    onClickTopics="createButtonClicked"
                  />
                  <s:url var="simulation_create_url" action="simulation-create" />
                  <sj:submit
                    listenTopics="doCreate_simulation"
                    href="%{simulation_create_url}"
                    targets="trash_box"
                    replaceTarget="false"
                    indicator="simulation_create_indicator"
                    validate="true"
                    validateFunction="validation"
                    onBeforeTopics="removeErrors"
                    onSuccessTopics="simulationTableUpdated"
                    onErrorTopics="createError"
                    clearForm="true"
                    cssStyle="display: none;"
                  />
                </td>
                <td class="two-buttons-left-indicator-cell">
                  <img
                    id="simulation_create_indicator"
                    src="images/loading_small.gif"
                    alt="Loading..."
                    style="display: none;"
                  />
                </td>
                <td class="two-buttons-second-cell">
                  <sj:submit
                    value="%{getText('form.updateButton.label')}"
                    button="true"
                    cssClass="giane-form-button"
                    onClickTopics="updateButtonClicked"
                  />
                  <s:url var="simulation_update_url" action="simulation-update" />
                  <sj:submit
                    listenTopics="doUpdate_simulation"
                    href="%{simulation_update_url}"
                    targets="trash_box"
                    replaceTarget="false"
                    indicator="simulation_update_indicator"
                    validate="true"
                    validateFunction="validation"
                    onBeforeTopics="removeErrors"
                    onSuccessTopics="simulationTableUpdated"
                    onErrorTopics="updateError"
                    clearForm="true"
                    cssStyle="display: none;"
                  />
                  <img
                    id="simulation_update_indicator"
                    src="images/loading_small.gif"
                    alt="Loading..."
                    style="display: none;"
                  />
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </fieldset>
    </s:form>
  </div>
</div>

<s:url var="simulation_grid_box_url" action="simulation-grid-box" escapeAmp="false">
  <s:param name="hide_running" value="true" />
  <s:param name="grid_id" value="'simulation_grid'" />
  <s:param name="navigatorDelete" value="true" />
  <s:param name="multiselect" value="true" />
  <s:param name="navigatorExtraButtons">
  {
    config: { 
      title: 'Configure selected item',
      icon: 'ui-icon-gear',
      topic: 'gridConfigButtonClicked'
    },
    delete: { 
      title: 'Delete selected item',
      icon: 'ui-icon-trash',
      topic: 'gridDeleteButtonClicked'
    }
  }
  </s:param>
  <s:param name="onDblClickRowTopics" value="'simulation_rowDblClicked'" />
  <s:param name="reloadTopics" value="'simulationTableUpdated'" />
</s:url>
<div class="giane-tab-content-grid-column">
  <sj:div href="%{simulation_grid_box_url}" indicator="simulation_grid_box_indicator" cssClass="giane-grid-box" />
  <img id="simulation_grid_box_indicator" src="images/loading_small.gif" alt="Loading..." style="display: none;" />
</div>

<s:url var="simulation_url" action="simulation" escapeAmp="false">
  <s:param name="tabIndex" value="%{#parameters.tabIndex}" />
  <s:param name="breadcrumbsId" value="%{#parameters.breadcrumbsId}" />
</s:url>
<sj:submit
  href="%{simulation_url}"
  formIds="simulation_form"
  targets="config_main"
  replaceTarget="false"
  indicator="config_main_indicator"
  validate="true"
  validateFunction="checkRowSelection"
  listenTopics="simulation_rowDblClicked"
  onBeforeTopics="mainPaneGoingForward_before"
  onAfterValidationTopics="mainPaneGoingForward_after"
  onCompleteTopics="mainPaneCompleted"
  cssStyle="display: none;"
/>
