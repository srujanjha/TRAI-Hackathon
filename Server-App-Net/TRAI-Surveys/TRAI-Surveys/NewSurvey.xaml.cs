using Microsoft.WindowsAzure.MobileServices;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace TRAI_Surveys
{
    /// <summary>
    /// Interaction logic for NewSurvey.xaml
    /// </summary>
    public partial class NewSurvey : Window
    {
        public static MobileServiceClient MobileService = new MobileServiceClient("https://trai.azure-mobile.net/", "sKVAklhSawgSCrrQHfOfSDnYqVUiOR89");

        public NewSurvey()
        {
            InitializeComponent();
        }

        private async void button_Click(object sender, RoutedEventArgs e)
        {
            button.IsEnabled = false;
            Questions item = new Questions { Question = textBox.Text, Option1 = text1.Text, Option2 = text2.Text, Option3 = text3.Text,Done=false };
            await MobileService.GetTable<Questions>().InsertAsync(item);
            this.Close();
        }
    }
}
