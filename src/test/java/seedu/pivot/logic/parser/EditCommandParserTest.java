package seedu.pivot.logic.parser;

import static seedu.pivot.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.pivot.logic.commands.testutil.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.pivot.logic.commands.testutil.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.pivot.logic.commands.testutil.CommandTestUtil.NAME_DESC_AMY;
import static seedu.pivot.logic.commands.testutil.CommandTestUtil.NAME_DESC_BOB;
import static seedu.pivot.logic.commands.testutil.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.pivot.logic.commands.testutil.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.pivot.logic.commands.testutil.CommandTestUtil.VALID_NAME_AMY;
import static seedu.pivot.logic.commands.testutil.CommandTestUtil.VALID_NAME_BOB;
import static seedu.pivot.logic.commands.testutil.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.pivot.logic.commands.testutil.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.pivot.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.pivot.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.pivot.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.pivot.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.pivot.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.pivot.testutil.TypicalIndexes.INDEX_THIRD_PERSON;

import org.junit.jupiter.api.Test;

import seedu.pivot.commons.core.index.Index;
import seedu.pivot.logic.commands.EditCommand;
import seedu.pivot.logic.commands.EditCommand.EditCaseDescriptor;
import seedu.pivot.model.investigationcase.Title;
import seedu.pivot.model.tag.Tag;
import seedu.pivot.testutil.EditCaseDescriptorBuilder;

public class EditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_NAME_DESC, Title.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "1" + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS); // invalid tag

        // TODO: can have additional test case of invalid var1 followed by valid var2 here as well if needed.

        // valid name followed by invalid name. The test case for invalid name followed by valid name
        // is tested at {@code parse_invalidValueFollowedByValidValue_success()}
        assertParseFailure(parser, "1" + NAME_DESC_BOB + INVALID_NAME_DESC, Title.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Person} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, "1" + TAG_DESC_FRIEND + TAG_DESC_HUSBAND + TAG_EMPTY, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_DESC_FRIEND + TAG_EMPTY + TAG_DESC_HUSBAND, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_EMPTY + TAG_DESC_FRIEND + TAG_DESC_HUSBAND, Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        // TODO: originally test for two different invalid fields, changed to 2 INVALID_NAME_DESC
        //  for curr implementation)
        assertParseFailure(parser, "1" + INVALID_NAME_DESC + INVALID_NAME_DESC,
                Title.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_PERSON;
        String userInput = targetIndex.getOneBased() + TAG_DESC_HUSBAND + NAME_DESC_AMY + TAG_DESC_FRIEND;
        EditCaseDescriptor descriptor = new EditCaseDescriptorBuilder().withTitle(VALID_NAME_AMY)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    // TODO: Might need if we adding more fields, currently is same as parse_oneField test
    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + NAME_DESC_AMY;

        EditCaseDescriptor descriptor = new EditCaseDescriptorBuilder().withTitle(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_PERSON;
        String userInput = targetIndex.getOneBased() + NAME_DESC_AMY;
        EditCaseDescriptor descriptor = new EditCaseDescriptorBuilder().withTitle(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = targetIndex.getOneBased() + TAG_DESC_FRIEND;
        descriptor = new EditCaseDescriptorBuilder().withTags(VALID_TAG_FRIEND).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_acceptsLast() {
        Index targetIndex = INDEX_FIRST_PERSON;

        String userInput = targetIndex.getOneBased()
                + TAG_DESC_FRIEND + TAG_DESC_FRIEND + TAG_DESC_HUSBAND;

        EditCaseDescriptor descriptor = new EditCaseDescriptorBuilder()
                .withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND)
                .build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_invalidValueFollowedByValidValue_success() {
        // no other valid values specified
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + INVALID_NAME_DESC + NAME_DESC_BOB;
        EditCaseDescriptor descriptor = new EditCaseDescriptorBuilder().withTitle(VALID_NAME_BOB).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // TODO: Might need if we are adding more field, to add other valid values if there are
        // other valid values specified
        userInput = targetIndex.getOneBased() + INVALID_NAME_DESC
                + NAME_DESC_BOB;
        descriptor = new EditCaseDescriptorBuilder().withTitle(VALID_NAME_BOB)
                .build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_resetTags_success() {
        Index targetIndex = INDEX_THIRD_PERSON;
        String userInput = targetIndex.getOneBased() + TAG_EMPTY;

        EditCaseDescriptor descriptor = new EditCaseDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }
}
