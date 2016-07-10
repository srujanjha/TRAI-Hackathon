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
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace TRAI_Surveys
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public static MobileServiceClient MobileService = new MobileServiceClient("https://trai.azure-mobile.net/","sKVAklhSawgSCrrQHfOfSDnYqVUiOR89");
        
        public MainWindow()
        {
            InitializeComponent();
            refresh();
        }
        bool refreshB = false, refreshA = false;
        public async void refresh()
        {
            if (refreshB) return;
            refreshB = true;
            try
            {
                var item = await MobileService.GetTable<Questions>().ToListAsync();
                quesStack.ItemsSource = item;
                refreshB = false;
            }
            catch (Exception) { refreshB = false; }
        }

        private async void btnRefresh_Click(object sender, RoutedEventArgs e)
        {
            //Response item = new Response { QuestionId= "C0820FBA-F799-4E33-9E95-52FC0219246E", Answer = 1};
            //await MobileService.GetTable<Response>().InsertAsync(item);
            refresh();
        }

        private void button_Click(object sender, RoutedEventArgs e)
        {
            NewSurvey forms = new NewSurvey();
            forms.Show();
        }
        private async void btn_Automate(object sender, RoutedEventArgs e)
        {
            if (refreshB) return;
            refreshB = true;
            var item0 = await MobileService.GetTable<CallDrops>().ToListAsync();
            long cd = 0, ac = 0;
            foreach(CallDrops c in item0)
            {
                cd += Convert.ToInt64(c.C_cd);
                ac += Convert.ToInt64(c.C_ac);
            }
            if(cd/(double)ac>0.5)
            {
                Questions item = new Questions { Question = "Are you experiencing Call Drops ?", Option1 = "Yes", Option2 = "No", Option3 = "Can't Say", Done = false };
                await MobileService.GetTable<Questions>().InsertAsync(item);
                MessageBox.Show("Survey Generated");
            }
            else MessageBox.Show("NO Survey Generated. Number of call-drops isn't above threshold:"+ cd / (double)ac);
            refreshA = false;
        }

        private async void quesStack_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            Questions ques = (Questions)quesStack.SelectedItem;
            try
            {
                var item0 = await MobileService.GetTable<Response>().Where(e1 => e1.QuestionId == ques.Id).Where(e1 => e1.Answer == 0).ToListAsync();
                var item1 = await MobileService.GetTable<Response>().Where(e1 => e1.QuestionId == ques.Id).Where(e1 => e1.Answer == 1).ToListAsync();
                var item2 = await MobileService.GetTable<Response>().Where(e1 => e1.QuestionId == ques.Id).Where(e1 => e1.Answer == 2).ToListAsync();
                Graph g = new Graph(new string[] { ques.Option1,ques.Option2,ques.Option3},new int[] { item0.Count, item1.Count, item2.Count},ques.Question);
                g.Show();
            }
            catch (Exception e2)
            { }

        }
    }
}
