/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.neo4j.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Assert;
import org.junit.Test;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import static org.hamcrest.CoreMatchers.is;

public class Neo4jQueryNodeTest extends Neo4jJdbcTest
{

    public Neo4jQueryNodeTest( Mode mode ) throws SQLException
    {
        super( mode );
    }

    @Test
    public void testGetTables() throws SQLException
    {
        ResultSet rs = conn.getMetaData().getTables( null, null, "%", null );

        while ( rs.next() )
        {
            System.out.println( rs.getString( "TABLE_NAME" ) );
        }
    }

    @Test
    public void testRetrieveNodes() throws SQLException
    {
        createData( gdb );
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( "match (n) optional match p=(n)-[r]-(m) return n,r,m,p,ID(n)," +
                "length(p),n.name as name limit 5" );
        int count = 0;
        ResultSetMetaData metaData = rs.getMetaData();
        int cols = metaData.getColumnCount();
        Assert.assertThat( cols, is( 7 ) );
        while ( rs.next() )
        {
            for ( int i = 1; i <= cols; i++ )
            {
                //String columnName = metaData.getColumnName(i);
                //System.out.println(columnName);
                System.out.print( rs.getObject( i ) + "\t" );
            }
            System.out.println();
            count++;
        }

        Assert.assertThat( count, is( 5 ) );
    }

    private void createData( GraphDatabaseService gdb )
    {
        try ( Transaction tx = gdb.beginTx() )
        {
            final Node n1 = gdb.createNode();
            n1.setProperty( "name", "n1" );
            final Node n2 = gdb.createNode();
            final Node n3 = gdb.createNode();
            final Node n4 = gdb.createNode();
            final Node n5 = gdb.createNode();
            final Relationship rel1 = n1.createRelationshipTo( n2, DynamicRelationshipType.withName( "REL" ) );
            rel1.setProperty( "name", "rel1" );
            tx.success();
        }
    }
}

