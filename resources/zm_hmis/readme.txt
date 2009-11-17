These SQL scripts will create a table, essentially a materialized view, that
characterizes the total number of data elements submitted by a
facility, compared to the number expected in each data set. One would not expect that every facility will submit data on
every single data element, as there are variations from facility to
facility in terms of what services are available. Additionally, for
some disease specific data elements, the facility may not report
anything for a given month, if they had no occurrence of this
syndrome. The hypothesis is that most facilities should be rather
constant in terms of the percentage of returned data. Some months it
may be higher, some lower, but in general, the percentage of submitted
data elements per facility should remain relatively constant. Of
course the easier situation is when facilities report nothing, which
of course will be easier to evaluate.

These SQL scripts take a slightly different view on the "data completeness" functionality of DHIS2 itself, which requires the user to mark the dataset as complete. Several BIRT reports have been included along with the SQL script. They will require modification in each country, specifially on the JDBC connection string and username/authentication. 

For further information on these reports and SQL contact Jason Pickering <jason.p.pickering@gmail.com>. 
