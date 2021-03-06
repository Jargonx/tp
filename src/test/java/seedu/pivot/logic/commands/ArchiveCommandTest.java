package seedu.pivot.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.pivot.commons.core.UserMessages.MESSAGE_INVALID_CASE_DISPLAYED_INDEX;
import static seedu.pivot.testutil.Assert.assertThrows;
import static seedu.pivot.testutil.TypicalCases.getTypicalPivot;
import static seedu.pivot.testutil.TypicalIndexes.FIRST_INDEX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.pivot.commons.core.index.Index;
import seedu.pivot.logic.commands.archivecommands.ArchiveCommand;
import seedu.pivot.logic.commands.exceptions.CommandException;
import seedu.pivot.logic.commands.testutil.ModelStub;
import seedu.pivot.logic.state.StateManager;
import seedu.pivot.model.Model;
import seedu.pivot.model.ModelManager;
import seedu.pivot.model.UserPrefs;
import seedu.pivot.model.investigationcase.ArchiveStatus;
import seedu.pivot.model.investigationcase.Case;
import seedu.pivot.testutil.CaseBuilder;



public class ArchiveCommandTest {

    private Model model;

    @BeforeEach
    public void setUpDefaultSection() {
        model = new ModelManager(getTypicalPivot(), new UserPrefs());
        StateManager.resetState();
        StateManager.setDefaultSection();
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ArchiveCommand(null));
    }

    @Test
    public void equals() {
        Index alternateCaseIndex = Index.fromZeroBased(1000);
        ArchiveCommand command = new ArchiveCommand(FIRST_INDEX);

        // same object -> returns true
        assertTrue(command.equals(command));

        // same values -> returns true
        assertTrue(command.equals(new ArchiveCommand(FIRST_INDEX)));

        // different types -> returns false
        assertFalse(command.equals(1));

        // null -> returns false
        assertFalse(command.equals(null));

        // different index -> returns false
        assertFalse(command.equals(new ArchiveCommand(alternateCaseIndex)));
    }

    @Test
    public void execute_validIndex_success() throws Exception {
        ModelStubWithCaseList modelStub = new ModelStubWithCaseList();
        Case validCase = new CaseBuilder().build();
        modelStub.addCase(validCase);

        CommandResult commandResult = new ArchiveCommand(FIRST_INDEX).execute(modelStub);
        Case archivedCase = new CaseBuilder().withArchiveStatus(ArchiveStatus.ARCHIVED).build();

        assertEquals(String.format(ArchiveCommand.MESSAGE_ARCHIVE_CASE_SUCCESS, validCase),
                commandResult.getFeedbackToUser());
        assertEquals(Arrays.asList(archivedCase), modelStub.caseList); // case list updated with archived version
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredCaseList().size() + 1);
        ModelStubWithCaseList modelStub = new ModelStubWithCaseList();
        ArchiveCommand archiveCommand = new ArchiveCommand(outOfBoundIndex);

        assertThrows(CommandException.class, MESSAGE_INVALID_CASE_DISPLAYED_INDEX, () ->
                archiveCommand.execute(modelStub));
    }

    /**
     * A Model stub that holds a caseList.
     */
    private class ModelStubWithCaseList extends ModelStub {
        final List<Case> caseList;

        private ModelStubWithCaseList() {
            this.caseList = new ArrayList<>();
        }

        @Override
        public ObservableList<Case> getFilteredCaseList() {
            return FXCollections.observableList(caseList);
        }

        @Override
        public void addCase(Case investigationCase) {
            requireNonNull(investigationCase);
            caseList.add(investigationCase);
        }

        @Override
        public void deleteCase(Case investigationCase) {
            requireNonNull(investigationCase);
            caseList.remove(investigationCase);
        }

        @Override
        public void updateFilteredCaseList(Predicate<Case> predicate) {
            caseList.stream().filter(predicate);
        }

        @Override
        public void commitPivot(String commandMessage, Undoable command) {}
    }

}
