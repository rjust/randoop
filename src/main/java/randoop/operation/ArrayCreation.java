package randoop.operation;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Objects;
import randoop.ExecutionOutcome;
import randoop.NormalExecution;
import randoop.sequence.Statement;
import randoop.sequence.Variable;
import randoop.types.ArrayType;
import randoop.types.Type;
import randoop.types.TypeTuple;

/**
 * {@code ArrayCreation} is a {@link Operation} representing the construction of a one-dimensional
 * array of a given type. The operation takes a length argument and creates an array of that size.
 */
public class ArrayCreation extends CallableOperation {

  /** The element type for the created array */
  private final Type elementType;

  /** The component type for the created array */
  private final Type componentType;

  /** The dimensions of the created array */
  private int dimensions;

  /**
   * Creates an object representing the construction of an array of the given type.
   *
   * @param arrayType the type of the created array
   */
  ArrayCreation(ArrayType arrayType) {
    this.elementType = arrayType.getElementType();
    this.componentType = arrayType.getComponentType();
    this.dimensions = arrayType.getDimensions();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ArrayCreation)) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    ArrayCreation arrayCreation = (ArrayCreation) obj;
    return this.elementType.equals(arrayCreation.elementType)
        && this.dimensions == arrayCreation.dimensions;
  }

  @Override
  public int hashCode() {
    return Objects.hash(elementType, dimensions);
  }

  @Override
  public String toString() {
    String result = elementType.getName();
    for (int i = 0; i < dimensions; i++) {
      result += "[]";
    }
    return result;
  }

  @Override
  public ExecutionOutcome execute(Object[] input, PrintStream out) {
    assert input.length == 1 : "requires array dimension as input";
    int length = Integer.parseInt(input[0].toString());
    long startTime = System.currentTimeMillis();
    Object theArray = Array.newInstance(this.componentType.getRuntimeClass(), length);
    long totalTime = System.currentTimeMillis() - startTime;
    return new NormalExecution(theArray, totalTime);
  }

  @Override
  public void appendCode(
      Type declaringType,
      TypeTuple inputTypes,
      Type outputType,
      List<Variable> inputVars,
      StringBuilder b) {
    Variable inputVar = inputVars.get(0);
    b.append("new").append(" ").append(this.elementType.getName());
    b.append("[ ");
    String param = inputVar.getName();
    Statement statementCreatingVar = inputVar.getDeclaringStatement();
    if (statementCreatingVar.isPrimitiveInitialization()
        && !statementCreatingVar.isNullInitialization()) {
      String shortForm = statementCreatingVar.getShortForm();
      if (shortForm != null) {
        param = shortForm;
      }
    }
    b.append(param).append(" ]");
    for (int i = 1; i < dimensions; i++) {
      b.append("[]");
    }
  }

  @Override
  public String toParsableString(Type declaringType, TypeTuple inputTypes, Type outputType) {
    String result = elementType.getName() + "[ " + inputTypes.get(0) + " ]";
    for (int i = 1; i < dimensions; i++) {
      result += "[]";
    }
    return result;
  }

  @Override
  public String getName() {
    return this.toString();
  }
}
