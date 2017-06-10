package fr.tvbarthel.apps.shapi.game;

import fr.tvbarthel.apps.shapi.audioeffect.AudioEffectEngine;
import fr.tvbarthel.apps.shapi.audioeffect.AudioEffects;
import fr.tvbarthel.apps.shapi.shape.Shape;
import fr.tvbarthel.apps.shapi.shape.generation.ShapeGenerator;
import fr.tvbarthel.apps.shapi.shape.identification.ShapeIdentifier;

/**
 * An engine responsible of the game logic.
 */
public class GameEngine {

    private static final int RIGHT_ANSWERS_BEFORE_LEVEL_2 = 2;
    private static final int RIGHT_ANSWERS_BEFORE_LEVEL_3 = 6;

    private final ShapeIdentifier shapeIdentifier;
    private final ShapeGenerator shapeGenerator;
    private final AudioEffectEngine audioEffectEngine;
    private final FieldGenerator fieldGenerator;
    private Shape currentShape;
    private Field currentField;

    /**
     * Create a new {@link GameEngine}.
     *
     * @param shapeIdentifier   a {@link ShapeIdentifier}
     * @param shapeGenerator    a {@link ShapeGenerator}
     * @param audioEffectEngine an {@link AudioEffectEngine}
     * @param fieldGenerator    a {@link FieldGenerator}
     */
    GameEngine(ShapeIdentifier shapeIdentifier, ShapeGenerator shapeGenerator,
               AudioEffectEngine audioEffectEngine, FieldGenerator fieldGenerator) {
        this.shapeIdentifier = shapeIdentifier;
        this.shapeGenerator = shapeGenerator;
        this.audioEffectEngine = audioEffectEngine;
        this.fieldGenerator = fieldGenerator;

        this.currentShape = this.shapeGenerator.generate();
        this.currentField = generateNewField();
    }

    /**
     * Get the current {@link Shape} that needs to be identified.
     *
     * @return a {@link Shape} that the user needs to identify.
     */
    Shape getCurrentShapeToIdentify() {
        return currentShape;
    }

    /**
     * Identify the current {@link Shape}.
     * <p>
     * <b>See</b> {@link GameEngine#getCurrentShapeToIdentify()}
     *
     * @param classz the supposed {@link Class} of the current {@link Shape}
     * @return True if the current {@link Shape} has been correctly identified, false otherwise.
     */
    boolean identifyCurrentShape(Class<? extends Shape> classz) {
        if (shapeIdentifier.identify(currentShape, classz)) {
            audioEffectEngine.play(AudioEffects.AUDIO_EFFECT_ID_SUCCESS);
            currentShape = shapeGenerator.generate();
            currentField = generateNewField();
            return true;
        } else {
            audioEffectEngine.play(AudioEffects.AUDIO_EFFECT_ID_FAILURE);
            return false;
        }
    }

    /**
     * Get the current {@link GameScore}.
     *
     * @return the current {@link GameScore}.
     */
    GameScore getCurrentScore() {
        return new GameScore(shapeIdentifier.getCorrectlyIdentifiedShapes().size(),
                shapeIdentifier.getIncorrectlyIdentifiedShapes().size());
    }

    /**
     * Get the current {@link Field}.
     *
     * @return the current {@link Field}.
     */
    Field getCurrentField() {
        return currentField;
    }

    /**
     * Reset this {@link GameEngine}
     * to its initial state.
     */
    public void reset() {
        shapeIdentifier.reset();
        this.currentShape = this.shapeGenerator.generate();
        this.currentField = generateNewField();
    }

    private Field generateNewField() {
        final int numberOfCorrectAnswers = getCurrentScore().getNumberOfCorrectAnswers();
        if (numberOfCorrectAnswers < RIGHT_ANSWERS_BEFORE_LEVEL_2) {
            return fieldGenerator.generateNewField(currentShape, GameLevels.LEVEL_1);
        } else if (numberOfCorrectAnswers < RIGHT_ANSWERS_BEFORE_LEVEL_3) {
            return fieldGenerator.generateNewField(currentShape, GameLevels.LEVEL_2);
        } else {
            return fieldGenerator.generateNewField(currentShape, GameLevels.LEVEL_3);
        }
    }


}
