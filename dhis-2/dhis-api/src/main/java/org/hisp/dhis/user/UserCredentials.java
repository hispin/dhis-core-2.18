package org.hisp.dhis.user;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DimensionType;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.common.annotation.Scanned;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dataelement.CategoryOptionGroupSet;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataset.DataSet;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Nguyen Hong Duc
 */
@JacksonXmlRootElement( localName = "userCredentials", namespace = DxfNamespaces.DXF_2_0 )
public class UserCredentials
    extends BaseIdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -8919501679702302098L;

    /**
     * Required and unique.
     * TODO: This must be renamed before we start using idObjectStore for UserCredentials
     */
    //private User user;

    /**
     * Required and unique.
     */
    private String username;

    /**
     * Unique OpenID.
     */
    private String openId;

    /**
     * Required. Will be stored as a hash.
     */
    private String password;

    /**
     * Date when password was changed.
     */
    private Date passwordLastUpdated;

    /**
     * Set of user roles.
     */
    @Scanned
    private Set<UserAuthorityGroup> userAuthorityGroups = new HashSet<>();

    /**
     * Category option group set dimensions to constrain data analytics aggregation.
     */
    @Scanned
    private Set<CategoryOptionGroupSet> cogsDimensionConstraints = new HashSet<>();

    /**
     * Category dimensions to constrain data analytics aggregation.
     */
    @Scanned
    private Set<DataElementCategory> catDimensionConstraints = new HashSet<>();

    /**
     * Date of the user's last login.
     */
    private Date lastLogin;

    /**
     * The token used for a user account restore. Will be stored as a hash.
     */
    private String restoreToken;

    /**
     * The code used for a user account restore. Will be stored as a hash.
     */
    private String restoreCode;

    /**
     * The timestamp representing when the restore window expires.
     */
    private Date restoreExpiry;

    /**
     * Indicates whether this user was originally self registered.
     */
    private boolean selfRegistered;

    /**
     * Indicates whether this is user is disabled, which means the user cannot
     * be authenticated.
     */
    private boolean disabled;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public UserCredentials()
    {
        setAutoFields();
        this.name = this.getClass().getSimpleName(); // Used to avoid JSR-303 issues
        this.lastLogin = new Date();
        this.passwordLastUpdated = new Date();
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    /**
     * Returns a concatenated String of the display names of all user authority
     * groups for this user credentials.
     */
    public String getUserAuthorityGroupsName()
    {
        return IdentifiableObjectUtils.join( userAuthorityGroups );
    }

    /**
     * Returns a set of the aggregated authorities for all user authority groups
     * of this user credentials.
     */
    public Set<String> getAllAuthorities()
    {
        Set<String> authorities = new HashSet<>();

        for ( UserAuthorityGroup group : userAuthorityGroups )
        {
            authorities.addAll( group.getAuthorities() );
        }

        return authorities;
    }

    /**
     * Tests whether this user credentials has any of the authorities in the
     * given set.
     *
     * @param auths the authorities to compare with.
     * @return true or false.
     */
    public boolean hasAnyAuthority( Collection<String> auths )
    {
        Set<String> all = new HashSet<>( getAllAuthorities() );
        return all.removeAll( auths );
    }

    /**
     * Tests whether the user has the given authority. Returns true in any case
     * if the user has the ALL authority.
     */
    public boolean isAuthorized( String auth )
    {
        if ( auth == null )
        {
            return false;
        }

        final Set<String> auths = getAllAuthorities();

        return auths.contains( UserAuthorityGroup.AUTHORITY_ALL ) || auths.contains( auth );
    }

    /**
     * Indicates whether this user credentials is a super user, implying that the
     * ALL authority is present in at least one of the user authority groups of
     * this user credentials.
     */
    public boolean isSuper()
    {
        for ( UserAuthorityGroup group : userAuthorityGroups )
        {
            if ( group.isSuper() )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a set of the aggregated data sets for all user authority groups
     * of this user credentials.
     */
    public Set<DataSet> getAllDataSets()
    {
        Set<DataSet> dataSets = new HashSet<>();

        for ( UserAuthorityGroup group : userAuthorityGroups )
        {
            dataSets.addAll( group.getDataSets() );
        }

        return dataSets;
    }

    /**
     * Indicates whether this user credentials can issue the given user authority
     * group. First the given authority group must not be null. Second this
     * user credentials must not contain the given authority group. Third
     * the authority group must be a subset of the aggregated user authorities
     * of this user credentials, or this user credentials must have the ALL
     * authority.
     *
     * @param group                          the user authority group.
     * @param canGrantOwnUserAuthorityGroups indicates whether this users can grant
     *                                       its own authoritiy groups to others.
     */
    public boolean canIssue( UserAuthorityGroup group, boolean canGrantOwnUserAuthorityGroups )
    {
        if ( group == null )
        {
            return false;
        }

        final Set<String> authorities = getAllAuthorities();

        if ( authorities.contains( UserAuthorityGroup.AUTHORITY_ALL ) )
        {
            return true;
        }

        if ( !canGrantOwnUserAuthorityGroups && userAuthorityGroups.contains( group ) )
        {
            return false;
        }

        return authorities.containsAll( group.getAuthorities() );
    }

    /**
     * Indicates whether this user credentials can modify the given user
     * credentials. This user credentials must have the ALL authority or possess
     * all user authorities of the other user credentials to do so.
     *
     * @param other the user credentials to modify.
     */
    public boolean canModify( UserCredentials other )
    {
        if ( other == null )
        {
            return false;
        }

        final Set<String> authorities = getAllAuthorities();

        if ( authorities.contains( UserAuthorityGroup.AUTHORITY_ALL ) )
        {
            return true;
        }

        return authorities.containsAll( other.getAllAuthorities() );
    }

    /**
     * Indicates whether this user credentials can issue all of the user authority
     * groups in the given collection.
     *
     * @param groups                         the collection of user authority groups.
     * @param canGrantOwnUserAuthorityGroups indicates whether this users can grant
     *                                       its own authoritiy groups to others.
     */
    public boolean canIssueAll( Collection<UserAuthorityGroup> groups, boolean canGrantOwnUserAuthorityGroups )
    {
        for ( UserAuthorityGroup group : groups )
        {
            if ( !canIssue( group, canGrantOwnUserAuthorityGroups ) )
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Return the name of this user credentials. More specifically, if this
     * credentials has a user it will return the first name and surname of that
     * user, if not it returns the username of this credentials.
     *
     * @return the name.
     */
    public String getName()
    {
        return user != null ? user.getName() : username;
    }

    public String getCode()
    {
        return username;
    }

    /**
     * Tests whether the given input arguments can perform a valid restore of
     * the user account for these credentials.
     * <p>
     * If fail, returns one of the following error strings:
     * <ul>
     *     <li>account_restoreToken_is_null</li>
     *     <li>account_restoreCode_is_null</li>
     *     <li>account_restoreExpiry_is_null</li>
     *     <li>token_parameter_is_null</li>
     *     <li>code_parameter_is_null</li>
     *     <li>date_parameter_is_null</li>
     *     <li>token_does_not_match_restoreToken ...</li>
     *     <li>code_does_not_match_restoreCode ...</li>
     *     <li>date_is_after_expiry ...</li>
     * </ul>
     * @param token the restore token.
     * @param code  the restore code.
     * @param date  the expiry date.
     * @return null if success, or error message if fail.
     */
    public String canRestore( String token, String code, Date date )
    {
        if ( this.restoreToken == null )
        {
            return "account_restoreToken_is_null";
        }

        if ( this.restoreCode == null )
        {
            return "account_restoreCode_is_null";
        }

        if ( this.restoreExpiry == null )
        {
            return "account_restoreExpiry_is_null";
        }

        if ( token == null )
        {
            return "token_parameter_is_null";
        }

        if ( code == null )
        {
            return "code_parameter_is_null";
        }

        if ( date == null )
        {
            return "date_parameter_is_null";
        }

        if ( !token.equals ( this.restoreToken ) )
        {
            return ( "token_does_not_match_restoreToken - token: '" + token + "' restoreToken: '" + restoreToken + "'" );
        }

        if ( !code.equals ( this.restoreCode ) )
        {
            return ( "code_does_not_match_restoreCode - code: '" + code + "' restoreCode: '" + restoreCode + "'" );
        }

        if ( date.after( this.restoreExpiry ) )
        {
            return "date_is_after_expiry - date: " + date.toString() + " expiry: " + this.restoreExpiry.toString();
        }

        return null; // Success.
    }

    /**
     * Returns the dimensions to use as constrains (filters) in data analytics
     * aggregation.
     */
    public Set<DimensionalObject> getDimensionConstraints()
    {
        Set<DimensionalObject> constraints = new HashSet<>();

        for ( CategoryOptionGroupSet cogs : cogsDimensionConstraints )
        {
            cogs.setDimensionType( DimensionType.CATEGORYOPTION_GROUPSET );
            constraints.add( cogs );
        }

        for ( DataElementCategory cat : catDimensionConstraints )
        {
            cat.setDimensionType( DimensionType.CATEGORY );
            constraints.add( cat );
        }

        return constraints;
    }

    /**
     * Indicates whether this user has dimension constraints.
     */
    public boolean hasDimensionConstraints()
    {
        Set<DimensionalObject> constraints = getDimensionConstraints();
        return constraints != null && !constraints.isEmpty();
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return username.hashCode();
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

        if ( !(o instanceof UserCredentials) )
        {
            return false;
        }

        final UserCredentials other = (UserCredentials) o;

        return username.equals( other.getUsername() );
    }

    @Override
    public boolean haveUniqueNames()
    {
        return false;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonIgnore
    public String getPassword()
    {
        return password;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public void setPassword( String password )
    {
        this.password = password;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getPasswordLastUpdated()
    {
        return passwordLastUpdated;
    }

    public void setPasswordLastUpdated( Date passwordLastUpdated )
    {
        this.passwordLastUpdated = passwordLastUpdated;
    }

    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "userAuthorityGroups", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "userAuthorityGroup", namespace = DxfNamespaces.DXF_2_0 )
    public Set<UserAuthorityGroup> getUserAuthorityGroups()
    {
        return userAuthorityGroups;
    }

    public void setUserAuthorityGroups( Set<UserAuthorityGroup> userAuthorityGroups )
    {
        this.userAuthorityGroups = userAuthorityGroups;
    }

    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "catDimensionConstraints", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "catDimensionConstraint", namespace = DxfNamespaces.DXF_2_0 )
    public Set<DataElementCategory> getCatDimensionConstraints()
    {
        return catDimensionConstraints;
    }

    public void setCatDimensionConstraints( Set<DataElementCategory> catDimensionConstraints )
    {
        this.catDimensionConstraints = catDimensionConstraints;
    }

    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "cogsDimensionConstraints", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "cogsDimensionConstraint", namespace = DxfNamespaces.DXF_2_0 )
    public Set<CategoryOptionGroupSet> getCogsDimensionConstraints()
    {
        return cogsDimensionConstraints;
    }

    public void setCogsDimensionConstraints( Set<CategoryOptionGroupSet> cogsDimensionConstraints )
    {
        this.cogsDimensionConstraints = cogsDimensionConstraints;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getOpenId()
    {
        return openId;
    }

    public void setOpenId( String openId )
    {
        this.openId = openId;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getLastLogin()
    {
        return lastLogin;
    }

    public void setLastLogin( Date lastLogin )
    {
        this.lastLogin = lastLogin;
    }

    public String getRestoreToken()
    {
        return restoreToken;
    }

    public void setRestoreToken( String restoreToken )
    {
        this.restoreToken = restoreToken;
    }

    public String getRestoreCode()
    {
        return restoreCode;
    }

    public void setRestoreCode( String restoreCode )
    {
        this.restoreCode = restoreCode;
    }

    public Date getRestoreExpiry()
    {
        return restoreExpiry;
    }

    public void setRestoreExpiry( Date restoreExpiry )
    {
        this.restoreExpiry = restoreExpiry;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isSelfRegistered()
    {
        return selfRegistered;
    }

    public void setSelfRegistered( boolean selfRegistered )
    {
        this.selfRegistered = selfRegistered;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isDisabled()
    {
        return disabled;
    }

    public void setDisabled( boolean disabled )
    {
        this.disabled = disabled;
    }

    @Override
    public void mergeWith( IdentifiableObject other )
    {
        super.mergeWith( other );

        if ( other.getClass().isInstance( this ) )
        {
            UserCredentials userCredentials = (UserCredentials) other;

            openId = userCredentials.getOpenId();
            disabled = userCredentials.isDisabled();
            selfRegistered = userCredentials.isSelfRegistered();
            password = StringUtils.isEmpty( userCredentials.getPassword() ) ? password : userCredentials.getPassword();

            catDimensionConstraints.clear();
            catDimensionConstraints.addAll( userCredentials.getCatDimensionConstraints() );

            cogsDimensionConstraints.clear();
            cogsDimensionConstraints.addAll( userCredentials.getCogsDimensionConstraints() );

            userAuthorityGroups.clear();
            userAuthorityGroups.addAll( userCredentials.getUserAuthorityGroups() );
        }
    }

    @Override
    public String toString()
    {
        return "UserCredentials{" +
            "username='" + username + '\'' +
            ", openId='" + openId + '\'' +
            ", password='" + password + '\'' +
            ", passwordLastUpdated=" + passwordLastUpdated +
            ", userAuthorityGroups=" + userAuthorityGroups +
            ", lastLogin=" + lastLogin +
            ", restoreToken='" + restoreToken + '\'' +
            ", restoreCode='" + restoreCode + '\'' +
            ", restoreExpiry=" + restoreExpiry +
            ", selfRegistered=" + selfRegistered +
            ", disabled=" + disabled +
            '}';
    }
}
