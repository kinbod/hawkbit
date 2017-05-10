/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.eclipse.hawkbit.im.authentication.SpPermission.SpringEvalExpressions;
import org.eclipse.hawkbit.repository.exception.CancelActionNotAllowedException;
import org.eclipse.hawkbit.repository.exception.EntityNotFoundException;
import org.eclipse.hawkbit.repository.exception.IncompleteDistributionSetException;
import org.eclipse.hawkbit.repository.exception.RSQLParameterSyntaxException;
import org.eclipse.hawkbit.repository.exception.RSQLParameterUnsupportedFieldException;
import org.eclipse.hawkbit.repository.model.Action;
import org.eclipse.hawkbit.repository.model.Action.ActionType;
import org.eclipse.hawkbit.repository.model.Action.Status;
import org.eclipse.hawkbit.repository.model.ActionStatus;
import org.eclipse.hawkbit.repository.model.DistributionSet;
import org.eclipse.hawkbit.repository.model.DistributionSetAssignmentResult;
import org.eclipse.hawkbit.repository.model.DistributionSetType;
import org.eclipse.hawkbit.repository.model.SoftwareModuleType;
import org.eclipse.hawkbit.repository.model.Target;
import org.eclipse.hawkbit.repository.model.TargetWithActionType;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * A DeploymentManagement service provides operations for the deployment of
 * {@link DistributionSet}s to {@link Target}s.
 *
 */
public interface DeploymentManagement {

    /**
     * method assigns the {@link DistributionSet} to all {@link Target}s by
     * their IDs with a specific {@link ActionType} and {@code forcetime}.
     *
     * @param dsID
     *            the ID of the distribution set to assign
     * @param actionType
     *            the type of the action to apply on the assignment
     * @param forcedTimestamp
     *            the time when the action should be forced, only necessary for
     *            {@link ActionType#TIMEFORCED}
     * @param controllerIDs
     *            the IDs of the target to assign the distribution set
     * @return the assignment result
     *
     * @throws IncompleteDistributionSetException
     *             if mandatory {@link SoftwareModuleType} are not assigned as
     *             define by the {@link DistributionSetType}.
     *
     * @throws EntityNotFoundException
     *             if either provided {@link DistributionSet} or {@link Target}s
     *             do not exist
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_REPOSITORY_AND_UPDATE_TARGET)
    DistributionSetAssignmentResult assignDistributionSet(@NotNull Long dsID, @NotNull ActionType actionType,
            long forcedTimestamp, @NotEmpty Collection<String> controllerIDs);


    /**
     * method assigns the {@link DistributionSet} to all {@link Target}s by
     * their IDs with a specific {@link ActionType} and {@code forcetime}.
     *
     * @param dsID
     *            the ID of the distribution set to assign
     * @param targets
     *            a list of all targets and their action type
     * @return the assignment result
     *
     * @throws IncompleteDistributionSetException
     *             if mandatory {@link SoftwareModuleType} are not assigned as
     *             define by the {@link DistributionSetType}.
     *
     * @throws EntityNotFoundException
     *             if either provided {@link DistributionSet} or {@link Target}s
     *             do not exist
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_REPOSITORY_AND_UPDATE_TARGET)
    DistributionSetAssignmentResult assignDistributionSet(@NotNull Long dsID,
            @NotEmpty Collection<TargetWithActionType> targets);

    /**
     * method assigns the {@link DistributionSet} to all {@link Target}s by
     * their IDs with a specific {@link ActionType} and an action message.
     *
     * @param dsID
     *            the ID of the distribution set to assign
     * @param targets
     *            a list of all targets and their action type
     * @param actionMessage
     *            an optional message for the action status
     * @return the assignment result
     *
     * @throws IncompleteDistributionSetException
     *             if mandatory {@link SoftwareModuleType} are not assigned as
     *             define by the {@link DistributionSetType}.
     *
     * @throws EntityNotFoundException
     *             if either provided {@link DistributionSet} or {@link Target}s
     *             do not exist
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_REPOSITORY_AND_UPDATE_TARGET)
    DistributionSetAssignmentResult assignDistributionSet(@NotNull Long dsID,
            @NotEmpty Collection<TargetWithActionType> targets, String actionMessage);

    /**
     * Cancels given {@link Action} for given {@link Target}. The method will
     * immediately add a {@link Status#CANCELED} status to the action. However,
     * it might be possible that the controller will continue to work on the
     * cancellation.
     *
     * @param actionId
     *            to be canceled
     *
     * @return canceled {@link Action}
     * 
     * @throws CancelActionNotAllowedException
     *             in case the given action is not active or is already a cancel
     *             action
     * @throws EntityNotFoundException
     *             if action with given ID does not exist
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_UPDATE_TARGET)
    Action cancelAction(@NotNull Long actionId);

    /**
     * counts all actions associated to a specific target.
     *
     * @param rsqlParam
     *            rsql query string
     * @param controllerId
     *            the target associated to the actions to count
     * @return the count value of found actions associated to the target
     *
     * @throws RSQLParameterUnsupportedFieldException
     *             if a field in the RSQL string is used but not provided by the
     *             given {@code fieldNameProvider}
     * @throws RSQLParameterSyntaxException
     *             if the RSQL syntax is wrong
     * @throws EntityNotFoundException
     *             if target with given ID does not exist
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Long countActionsByTarget(@NotNull String rsqlParam, @NotEmpty String controllerId);

    /**
     * @return the total amount of stored action status
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Long countActionStatusAll();

    /**
     * @return the total amount of stored actions
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Long countActionsAll();

    /**
     * counts all actions associated to a specific target.
     *
     * @param controllerId
     *            the target associated to the actions to count
     * @return the count value of found actions associated to the target
     * 
     * @throws EntityNotFoundException
     *             if target with given ID does not exist
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Long countActionsByTarget(@NotEmpty String controllerId);

    /**
     * Get the {@link Action} entity for given actionId.
     *
     * @param actionId
     *            to be id of the action
     * @return the corresponding {@link Action}
     * 
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Optional<Action> findAction(@NotNull Long actionId);

    /**
     * Retrieves all {@link Action}s from repository.
     *
     * @param pageable
     *            pagination parameter
     * @return a paged list of {@link Action}s
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Slice<Action> findActionsAll(@NotNull Pageable pageable);

    /**
     * Retrieves all {@link Action} which assigned to a specific
     * {@link DistributionSet}.
     * 
     * @param pageable
     *            the page request parameter for paging and sorting the result
     * @param distributionSetId
     *            the distribution set which should be assigned to the actions
     *            in the result
     * @return a list of {@link Action} which are assigned to a specific
     *         {@link DistributionSet}
     * 
     * @throws EntityNotFoundException
     *             if distribution set with given ID does not exist
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Slice<Action> findActionsByDistributionSet(@NotNull Pageable pageable, @NotNull Long distributionSetId);

    /**
     * Retrieves all {@link Action}s assigned to a specific {@link Target} and a
     * given specification.
     *
     * @param rsqlParam
     *            rsql query string
     * @param controllerId
     *            the target which must be assigned to the actions
     * @param pageable
     *            the page request
     * @return a slice of actions assigned to the specific target and the
     *         specification
     * 
     * @throws RSQLParameterUnsupportedFieldException
     *             if a field in the RSQL string is used but not provided by the
     *             given {@code fieldNameProvider}
     * @throws RSQLParameterSyntaxException
     *             if the RSQL syntax is wrong
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Slice<Action> findActionsByTarget(@NotNull String rsqlParam, @NotEmpty String controllerId,
            @NotNull Pageable pageable);

    /**
     * Retrieves all {@link Action}s which are referring the given
     * {@link Target}.
     *
     * @param controllerId
     *            the target to find actions for
     * @param pageable
     *            the pageable request to limit, sort the actions
     * @return a slice of actions found for a specific target
     * 
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Slice<Action> findActionsByTarget(@NotEmpty String controllerId, @NotNull Pageable pageable);

    /**
     * Retrieves all the {@link ActionStatus} entries of the given
     * {@link Action} and {@link Target}.
     *
     * @param pageReq
     *            pagination parameter
     * @param actionId
     *            to be filtered on
     * @return the corresponding {@link Page} of {@link ActionStatus}
     * 
     * @throws EntityNotFoundException
     *             if action with given ID does not exist
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Page<ActionStatus> findActionStatusByAction(@NotNull Pageable pageReq, @NotNull Long actionId);

    /**
     * Retrieves all messages for an {@link ActionStatus}.
     *
     *
     * @param pageable
     *            the page request parameter for paging and sorting the result
     * @param actionStatusId
     *            the id of {@link ActionStatus} to retrieve the messages from
     * @return a page of messages by a specific {@link ActionStatus} id
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Page<String> findMessagesByActionStatusId(@NotNull Pageable pageable, @NotNull Long actionStatusId);

    /**
     * Get the {@link Action} entity for given actionId with all lazy attributes
     * (i.e. distributionSet, target, target.assignedDs).
     *
     * @param actionId
     *            to be id of the action
     * @return the corresponding {@link Action}
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Optional<Action> findActionWithDetails(@NotNull Long actionId);

    /**
     * Retrieves all active {@link Action}s of a specific target ordered by
     * action ID.
     * 
     * @param pageable
     *            the page request parameter for paging and sorting the result
     * @param controllerId
     *            the target associated with the actions
     * @return a list of actions associated with the given target
     * 
     * @throws EntityNotFoundException
     *             if target with given ID does not exist
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Page<Action> findActiveActionsByTarget(@NotNull Pageable pageable, @NotEmpty String controllerId);

    /**
     * Retrieves all inactive {@link Action}s of a specific target ordered by
     * action ID.
     *
     * @param pageable
     *            the page request parameter for paging and sorting the result
     * @param controllerId
     *            the target associated with the actions
     * @return a list of actions associated with the given target
     * 
     * @throws EntityNotFoundException
     *             if target with given ID does not exist
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Page<Action> findInActiveActionsByTarget(@NotNull Pageable pageable, @NotEmpty String controllerId);

    /**
     * Force cancels given {@link Action} for given {@link Target}. Force
     * canceling means that the action is marked as canceled on the SP server
     * and a cancel request is sent to the target. But however it's not tracked,
     * if the targets handles the cancel request or not.
     *
     * @param actionId
     *            to be canceled
     *
     * @return quite {@link Action}
     * 
     * @throws CancelActionNotAllowedException
     *             in case the given action is not active
     * 
     * @throws EntityNotFoundException
     *             if action with given ID does not exist
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_UPDATE_TARGET)
    Action forceQuitAction(@NotNull Long actionId);

    /**
     * Updates a {@link Action} and forces the {@link Action} if it's not
     * already forced.
     *
     * @param actionId
     *            the ID of the action
     * @return the updated or the found {@link Action}
     * 
     * @throws EntityNotFoundException
     *             if action with given ID does not exist
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_UPDATE_TARGET)
    Action forceTargetAction(@NotNull Long actionId);

    /**
     * Starts all scheduled actions of an RolloutGroup parent.
     *
     * @param rolloutId
     *            the rollout the actions belong to
     * @param rolloutGroupParentId
     *            the parent rollout group the actions should reference. null
     *            references the first group
     * @return the amount of started actions
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    long startScheduledActionsByRolloutGroupParent(@NotNull Long rolloutId, Long rolloutGroupParentId);

    /**
     * All {@link ActionStatus} entries in the repository.
     *
     * @param pageable
     *            the pagination parameter
     * @return {@link Page} of {@link ActionStatus} entries
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Page<ActionStatus> findActionStatusAll(@NotNull Pageable pageable);

    /**
     * Returns {@link DistributionSet} that is assigned to given {@link Target}.
     * 
     * @param controllerId
     *            of target
     * @return assigned {@link DistributionSet}
     * 
     * @throws EntityNotFoundException
     *             if target with given ID does not exist
     */
    Optional<DistributionSet> getAssignedDistributionSet(@NotEmpty String controllerId);

    /**
     * Returns {@link DistributionSet} that is installed on given
     * {@link Target}.
     * 
     * @param controllerId
     *            of target
     * @return installed {@link DistributionSet}
     * 
     * @throws EntityNotFoundException
     *             if target with given ID does not exist
     */
    Optional<DistributionSet> getInstalledDistributionSet(@NotEmpty String controllerId);
}
