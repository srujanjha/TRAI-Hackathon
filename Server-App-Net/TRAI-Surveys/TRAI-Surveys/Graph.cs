using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Windows.Forms.DataVisualization.Charting;

namespace TRAI_Surveys
{
    public partial class Graph : Form
    {
        public Graph(string[] seriesArray, int[] pointsArray, string question)
        {
            InitializeComponent();
            if (pointsArray[0] == 0 && pointsArray[1] == 0 && pointsArray[2] == 0)
            { MessageBox.Show("No responses for this survey yet!"); this.Close(); }
            // Set title.
            this.chart1.Titles.Add(question);
            Series series = this.chart1.Series[0];
            
            // Add series.
            for (int i = 0; i < seriesArray.Length; i++)
            {
                series.Points.Add(pointsArray[i]);
                series.Points[i].AxisLabel = seriesArray[i];
            }
        }
    }
}
