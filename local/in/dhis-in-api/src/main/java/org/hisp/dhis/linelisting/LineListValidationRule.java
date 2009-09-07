/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hisp.dhis.linelisting;

/**
 *
 * @author Administrator
 */
public class LineListValidationRule {

    /**
     * The unique identifier for validation rule.
     */
    private int id;

    /**
     * Name of validation rule.. Required and unique.
     */
    private String name;

    /**
     * Description of the validation rule..
     */
    private String description;

    /**
     * Operator used in the validation rule.
     */
    private String operator;

    /**
     * leftside of the operator in the validation rule
     */
    private String leftside;

     /**
     * rightside of the operator in the validation rule.
     */

    private String rightside;

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public LineListValidationRule()
    {
    }

    public LineListValidationRule(String name)
    {
        this.name = name;
    }

    public LineListValidationRule(String name, String operator, String leftside, String rightside)
    {
        this.name = name;
        this.operator = operator;
        this.leftside = leftside;
        this.rightside = rightside;
    }

    public LineListValidationRule(String name, String description, String operator, String leftside, String rightside)
    {
        this.name = name;
        this.description = description;
        this.operator = operator;
        this.leftside = leftside;
        this.rightside = rightside;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof LineListValidationRule) )
        {
            return false;
        }

        final LineListValidationRule other = (LineListValidationRule) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

    // -------------------------------------------------------------------------
    // Setter and Getter
    // -------------------------------------------------------------------------

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * @return the leftside
     */
    public String getLeftside() {
        return leftside;
    }

    /**
     * @param leftside the leftside to set
     */
    public void setLeftside(String leftside) {
        this.leftside = leftside;
    }

    /**
     * @return the rightside
     */
    public String getRightside() {
        return rightside;
    }

    /**
     * @param rightside the rightside to set
     */
    public void setRightside(String rightside) {
        this.rightside = rightside;
    }


    
}
