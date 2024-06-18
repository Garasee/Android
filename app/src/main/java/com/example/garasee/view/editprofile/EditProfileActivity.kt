package com.example.garasee.view.editprofile

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.garasee.R
import com.example.garasee.data.api.ErrorResponse
import com.example.garasee.databinding.ActivityEditProfileBinding
import com.example.garasee.di.Injection
import com.example.garasee.repository.UserRepository
import com.example.garasee.view.main.MainActivity
import com.google.gson.Gson
import com.google.i18n.phonenumbers.PhoneNumberUtil
import kotlinx.coroutines.launch
import retrofit2.HttpException

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var userRepository: UserRepository

    private val cityIdMap = mapOf(
        "Kabupaten Aceh Barat"	to	"936eba3b-de0d-441c-80ac-c19d3e34c6c4",
        "Kabupaten Aceh Barat Daya"	to	"83b65295-055e-495f-b81a-9629c3c1ff77",
        "Kabupaten Aceh Besar"	to	"c59bb59d-f2bd-4625-9a38-6c64c5064dc6",
        "Kabupaten Aceh Jaya"	to	"b64f3339-5e17-4a37-aa1b-ca8e6e142bbb",
        "Kabupaten Aceh Selatan"	to	"6a7ada14-9d5b-48bb-a9ad-01c154fca8d9",
        "Kabupaten Aceh Singkil"	to	"135d8e1b-7688-478d-a7b3-78c9709b5d0e",
        "Kabupaten Aceh Tamiang"	to	"3c894ac3-9705-448e-8f12-3675ea429c79",
        "Kabupaten Aceh Tengah"	to	"84783938-a33b-4347-8b02-212dfee916f7",
        "Kabupaten Aceh Tenggara"	to	"02cfbf45-7184-44a5-aa16-18e943d809a8",
        "Kabupaten Aceh Timur"	to	"d5b2124c-d0e2-49bb-845a-6e9483809b2d",
        "Kabupaten Aceh Utara"	to	"27494444-2d46-4e49-85b1-5dbba954ae0f",
        "Kabupaten Bener Meriah"	to	"a38e5500-fa3e-406b-ae7b-c767e853e4da",
        "Kabupaten Bireuen"	to	"baac42f0-cca4-4ff2-a775-1a8f0d0a8aa4",
        "Kabupaten Gayo Lues"	to	"862f92ee-469f-4026-b94e-7b6e4cf72883",
        "Kabupaten Nagan Raya"	to	"cb53ab92-649d-484c-8993-c9868d32cde3",
        "Kabupaten Pidie"	to	"50d10dc1-427e-4968-9cd6-70515c4b809d",
        "Kabupaten Pidie Jaya"	to	"99ce5331-9c61-41e8-9d39-c5f2750f4d14",
        "Kabupaten Simeulue"	to	"ffe0bb8b-41ef-4700-a88f-740b7f350dc6",
        "Kota Banda Aceh"	to	"ce2f0857-564d-4ab4-aa16-2dda9cbb9c36",
        "Kota Langsa"	to	"bc71f026-921a-4ddb-9bca-668c9a55b0d8",
        "Kota Lhokseumawe"	to	"a30006ed-2fb9-4f81-a53f-c04ef222b1ba",
        "Kota Sabang"	to	"4fbbd3db-1e41-4ccd-8cdc-046108047cab",
        "Kota Subulussalam"	to	"87860ba2-0389-4dd6-8951-55ad08425bed",
        "Kabupaten Asahan"	to	"2202020d-3dcb-4e5c-a2e7-a32f1393cb57",
        "Kabupaten Batu Bara"	to	"c2cda535-6472-4b93-afe9-33d2cfac1265",
        "Kabupaten Dairi"	to	"19431d5d-3174-4dfa-a8c6-598807bd55a7",
        "Kabupaten Deli Serdang"	to	"57a1c690-bd54-4ce9-a2af-99ba9319f384",
        "Kabupaten Humbang Hasundutan"	to	"aa589b79-2f97-46e0-9e2e-fb3094a925a9",
        "Kabupaten Karo"	to	"6211342a-78cd-4d28-9860-54e72b03949c",
        "Kabupaten Labuhanbatu"	to	"deadae0d-06f4-421f-b31f-d3e346277ba2",
        "Kabupaten Labuhanbatu Selatan"	to	"3a737994-cae1-4935-952c-77c33157027b",
        "Kabupaten Labuhanbatu Utara"	to	"e8261636-7a3a-4acf-9352-466bbe328b6c",
        "Kabupaten Langkat"	to	"cb60a8db-2fe6-4989-a3d2-755a15c257a9",
        "Kabupaten Mandailing Natal"	to	"209cbdde-f7a6-467a-bb74-cc9c86125867",
        "Kabupaten Nias"	to	"23b4590b-f668-4ee0-96cf-1b43eb7f7d17",
        "Kabupaten Nias Barat"	to	"6e2c625e-e8a5-496a-9dbc-a2473868159a",
        "Kabupaten Nias Selatan"	to	"de0ed852-aebb-4b11-aa72-f36f4a2b7cc9",
        "Kabupaten Nias Utara"	to	"a54d6860-f433-40eb-a117-854f71d846fa",
        "Kabupaten Padang Lawas"	to	"fe9a0c6f-2529-487d-844a-b092a42cedcc",
        "Kabupaten Padang Lawas Utara"	to	"c903a6ca-8804-4da8-89f5-376a3689deb5",
        "Kabupaten Pakpak Bharat"	to	"ff86df87-32d3-418f-822c-388a8d068eee",
        "Kabupaten Samosir"	to	"c123afd9-8b51-41d9-ab87-d2475172af60",
        "Kabupaten Serdang Bedagai"	to	"02b14ee2-1914-43a4-8bb6-04d762280f75",
        "Kabupaten Simalungun"	to	"295178d0-999c-4be3-b0cb-b63d9d551161",
        "Kabupaten Tapanuli Selatan"	to	"04fa42c0-a669-4336-9f33-8e54023d30f9",
        "Kabupaten Tapanuli Tengah"	to	"d66887b2-680a-4b1e-8767-099402f2b0a9",
        "Kabupaten Tapanuli Utara"	to	"2bec621a-de5b-4470-a29a-177d7d355e46",
        "Kabupaten Toba"	to	"c876d6b8-eca0-4774-a5c8-0dae0b54f55c",
        "Kota Binjai"	to	"3bbddeb4-2c20-46fc-bc51-dcbc4c94ae3f",
        "Kota Gunungsitoli"	to	"c208f43a-ae28-467e-ade1-5cac53db4cc6",
        "Kota Medan"	to	"5acb8c73-5c06-4b7a-b755-cede013dd788",
        "Kota Padang Sidempuan"	to	"7234bc3a-98f6-4361-ad79-ce3822a4816f",
        "Kota Pematangsiantar"	to	"f72daa0f-abc4-48fc-9eb3-8438b6dfb579",
        "Kota Sibolga"	to	"aeb63ac2-a0fc-4db9-a8ab-7301591acf06",
        "Kota Tanjungbalai"	to	"ba744bbf-f1d0-4c8b-b582-30a8eb59bf3e",
        "Kota Tebing Tinggi"	to	"d9615e24-ccee-4c62-8cf9-5ad8f30711af",
        "Kabupaten Agam"	to	"e8f76569-a77b-47ce-8d73-a19277f31956",
        "Kabupaten Dharmasraya"	to	"1308f1dd-d18b-4a31-a1e6-b7edf039c9f9",
        "Kabupaten Kepulauan Mentawai"	to	"fe35a09f-3df4-485d-a4c8-5bfb6040ca17",
        "Kabupaten Lima Puluh Kota"	to	"c3a6fd65-c8fd-4dc2-9c0a-c7fd85640cc3",
        "Kabupaten Padang Pariaman"	to	"045ed194-9269-4b44-b677-3cb5d1d36931",
        "Kabupaten Pasaman"	to	"13b3e443-7598-4091-9c1c-b149d5069ea2",
        "Kabupaten Pasaman Barat"	to	"9e76fb7f-1b5a-4ff0-8dc2-be6bd8384e47",
        "Kabupaten Pesisir Selatan"	to	"c545d430-f103-4a88-89ee-e3f390d1b854",
        "Kabupaten Sijunjung"	to	"c0c2ca4f-2853-4fe8-9646-9e3b1da1fc36",
        "Kabupaten Solok"	to	"a87ae1cd-a258-403b-bf30-696166896d99",
        "Kabupaten Solok Selatan"	to	"b4e1ca84-6eee-47ee-9fe0-1698b99efcc3",
        "Kabupaten Tanah Datar"	to	"b0010de7-012f-4682-9beb-c32f3610f55a",
        "Kota Bukittinggi"	to	"00d01f74-6b38-4b62-9734-0167388363cc",
        "Kota Padang"	to	"edbd792d-4abf-4594-a3ca-1102b0cfcac3",
        "Kota Padang Panjang"	to	"9475e8a9-306a-4191-b954-be3413f82b50",
        "Kota Pariaman"	to	"6a873577-9526-466a-a957-9f736d111b2c",
        "Kota Payakumbuh"	to	"e22b4bba-7042-4f8a-bef4-38c229c76057",
        "Kota Sawahlunto"	to	"ba3e25ee-6375-42a7-9fb3-72fdaca85c63",
        "Kota Solok"	to	"109aafc7-0a83-4fb4-8877-83501496e556",
        "Kabupaten Bengkalis"	to	"53633e04-fefb-4d07-942b-3c22db8acb1b",
        "Kabupaten Indragiri Hilir"	to	"7e529298-9424-4bdd-a1bc-8c99b5b59df7",
        "Kabupaten Indragiri Hulu"	to	"4135e8d7-b5bc-403e-bbe8-f648f6aa4de1",
        "Kabupaten Kampar"	to	"7e3be0e7-8067-4c24-92e4-10050caeeba7",
        "Kabupaten Kepulauan Meranti"	to	"7af63df2-c938-462d-9170-93887748606a",
        "Kabupaten Kuantan Singingi"	to	"70856dfd-d0e1-4a9d-a11b-1a7808f32dbd",
        "Kabupaten Pelalawan"	to	"276ba4b5-2186-4242-bd0f-560d36ad1d1b",
        "Kabupaten Rokan Hilir"	to	"2b1629d9-729e-453c-ada9-4a5858895fc7",
        "Kabupaten Rokan Hulu"	to	"8c0d682a-293c-4586-9e12-4fe3ae5ee758",
        "Kabupaten Siak"	to	"8c41b44e-47fb-424b-baee-033b3f426768",
        "Kota Dumai"	to	"6092c0f6-c352-478f-abf2-7e558363e478",
        "Kota Pekanbaru"	to	"35592e00-2bc7-4f21-b78f-856fd2844470",
        "Kabupaten Bintan"	to	"e90fbb29-23af-49d8-916b-ecd66a1e80b8",
        "Kabupaten Karimun"	to	"130c17d3-816f-4526-a7a4-8eea8ce68d2f",
        "Kabupaten Kepulauan Anambas"	to	"efc2c127-19a6-434d-959d-fb246dcd0f68",
        "Kabupaten Lingga"	to	"9a738026-5c93-45b1-adfc-c07d44105923",
        "Kabupaten Natuna"	to	"c4507089-3ac6-406c-ac6f-8eb4f2db9c3a",
        "Kota Batam"	to	"8c200129-b5a8-4d1f-adb4-2964f651abab",
        "Kota Tanjungpinang"	to	"5c3298a4-0631-44a5-b541-d8288103fbbd",
        "Kabupaten Batanghari"	to	"8636364a-fa90-4976-8341-4762928f0a11",
        "Kabupaten Bungo"	to	"d5a0404b-0c2f-4624-8250-c2304f6a87dc",
        "Kabupaten Kerinci"	to	"947ab820-57b6-4d07-9e7b-edfe53a6b21c",
        "Kabupaten Merangin"	to	"0ebd00dc-cd06-4fca-b583-2412a0508f68",
        "Kabupaten Muaro Jambi"	to	"4db84108-77dd-4acc-895d-3a5f6d9f64df",
        "Kabupaten Sarolangun"	to	"d14d392d-7bd7-4dfd-a4fc-1e452a94c023",
        "Kabupaten Tanjung Jabung Barat"	to	"8f4a97be-c422-4086-9f1f-908420441af8",
        "Kabupaten Tanjung Jabung Timur"	to	"7185e50e-166c-45d0-ae2f-4981538c87ef",
        "Kabupaten Tebo"	to	"5b22cb76-fa0a-42c4-81a2-c415ec8aefcd",
        "Kota Jambi"	to	"85b66796-f7f9-4718-abc4-3ef22cced8ec",
        "Kota Sungai Penuh"	to	"f0368375-037b-40d1-89fb-09b18034716f",
        "Kabupaten Bengkulu Selatan"	to	"01ce754a-49f2-4369-ab09-c926ec49f2f3",
        "Kabupaten Bengkulu Tengah"	to	"df548782-1d13-4f54-a3c3-7310b7649b42",
        "Kabupaten Bengkulu Utara"	to	"b4adf767-1bb9-4343-ae07-87dcfdc869c3",
        "Kabupaten Kaur"	to	"a27b19c4-be91-4730-adc8-8f4bb22b3b45",
        "Kabupaten Kepahiang"	to	"8d23e006-2918-4f9f-b670-6f3de1db8f22",
        "Kabupaten Lebong"	to	"eecd69d2-1151-4991-8781-9dee430e4999",
        "Kabupaten Mukomuko"	to	"7a715e66-dc7b-489e-98d8-0a2e5142d9d0",
        "Kabupaten Rejang Lebong"	to	"211c10be-4505-489f-ac9f-6f1702d8e2b6",
        "Kabupaten Seluma"	to	"79d6e7b7-046c-40ac-844d-e69f428b841d",
        "Kota Bengkulu"	to	"06c3e152-3889-473a-9bdc-f934eebb7017",
        "Kabupaten Banyuasin"	to	"d259f961-91f9-4054-a585-b101a449646f",
        "Kabupaten Empat Lawang"	to	"620b5dab-c578-41b6-b986-13c78ca7e057",
        "Kabupaten Lahat"	to	"aa8624db-34a3-44ef-993e-310bc6a05ce1",
        "Kabupaten Muara Enim"	to	"fd9a80ac-94e9-4ac5-a1af-49031a3b4f01",
        "Kabupaten Musi Banyuasin"	to	"d15f3e75-9bce-4489-be97-15e62118920c",
        "Kabupaten Musi Rawas"	to	"d12273d1-ba15-4339-915e-8ad31d369ad8",
        "Kabupaten Musi Rawas Utara"	to	"8bcec6d7-6d1d-42fd-9c59-82b2b4e28bde",
        "Kabupaten Ogan Ilir"	to	"7112c1e0-e9e4-4b2c-8ffe-eda89a5e3a6c",
        "Kabupaten Ogan Komering Ilir"	to	"9f6d5e89-f623-4328-842d-83365bbd3419",
        "Kabupaten Ogan Komering Ulu"	to	"561368e8-7e92-443d-be69-1f6cd4d05370",
        "Kabupaten Ogan Komering Ulu Selatan"	to	"119cb663-fee4-416a-952d-d4100d7887fb",
        "Kabupaten Ogan Komering Ulu Timur"	to	"dd9a2c80-7e61-4a04-84d5-aec27c082b59",
        "Kabupaten Penukal Abab Lematang Ilir"	to	"ec8533cf-a0f7-4276-a769-bf4cf1f1e803",
        "Kota Lubuklinggau"	to	"553a855f-39f9-45be-822c-0477f77c3d20",
        "Kota Pagar Alam"	to	"7f559273-7829-4b83-aca0-b93922a30b5e",
        "Kota Palembang"	to	"9cecc128-36ee-46ba-ad4e-32bbdf023152",
        "Kota Prabumulih"	to	"19c81d8e-49d9-4f41-b8f5-328f1a03221d",
        "Kabupaten Bangka"	to	"854109f9-0b59-4a40-ab0c-d72cf5f28f3a",
        "Kabupaten Bangka Barat"	to	"3d0f54aa-3084-4286-9baa-15a1b2b8ba16",
        "Kabupaten Bangka Selatan"	to	"eec40cb1-f906-41b9-b8f7-8647b0efc1d2",
        "Kabupaten Bangka Tengah"	to	"ca2e396e-c22f-4385-a253-e9fd21ff30eb",
        "Kabupaten Belitung"	to	"d27d178d-2d75-4dff-89ca-bdd1a2d65b23",
        "Kabupaten Belitung Timur"	to	"c8477b99-cb9b-4f83-8c14-6ee05b860356",
        "Kota Pangkalpinang"	to	"8ad805e9-800f-4ba8-b1e9-54faef6a26bf",
        "Kabupaten Lampung Barat"	to	"a28bb6d1-2916-4f14-a7bd-3614bc0281e8",
        "Kabupaten Lampung Selatan"	to	"e3f42804-5721-4229-88e6-8a457ef2b985",
        "Kabupaten Lampung Tengah"	to	"6a296ef2-7b72-4775-acbd-1f94f71f5713",
        "Kabupaten Lampung Timur"	to	"fc42d81b-0e43-42a4-a03e-29959a1d315c",
        "Kabupaten Lampung Utara"	to	"766da202-b465-4762-ba5f-22551844d656",
        "Kabupaten Mesuji"	to	"17645d79-2416-4649-b55f-a00185babaf5",
        "Kabupaten Pesawaran"	to	"0d06b312-adf5-4be5-908a-8fe3a8f621f9",
        "Kabupaten Pesisir Barat"	to	"301fb97c-68c6-4975-b576-574afa4a00fe",
        "Kabupaten Pringsewu"	to	"14ccbb98-35eb-42b8-b6fe-3f7b2da1d9d8",
        "Kabupaten Tanggamus"	to	"4bc82cbc-fd3d-47a5-a896-547b12fe8656",
        "Kabupaten Tulang Bawang"	to	"db9bada3-4c46-462c-83a4-a5fca6fc370d",
        "Kabupaten Tulang Bawang Barat"	to	"e3eae2bc-316f-48cc-8989-b296ff86cdc0",
        "Kabupaten Way Kanan"	to	"533d517e-b167-4dbf-b101-e4d94cfa68aa",
        "Kota Bandar Lampung"	to	"05be3c04-ed6b-402c-a7df-b98c797cf405",
        "Kota Metro"	to	"87ccdf5f-ced8-453a-8ace-103fcea41fbf",
        "Kabupaten Lebak"	to	"6e4e3fb5-f072-426c-9806-ece2464dce53",
        "Kabupaten Pandeglang"	to	"4280e592-ba3a-414d-824e-5a43895d835a",
        "Kabupaten Serang"	to	"f36861cc-dfb8-4a39-8bcd-ba8cb3de8390",
        "Kabupaten Tangerang"	to	"0e1028c0-0997-416d-bc95-18ce5d651561",
        "Kota Cilegon"	to	"780d29c9-8b67-4c33-9526-7bbad4384bf6",
        "Kota Serang"	to	"7b8596bd-c2d4-4a5c-a78e-fcab5c74cd6d",
        "Kota Tangerang"	to	"3f5c41d1-80f3-4b3e-a8e5-4aee6225af32",
        "Kota Tangerang Selatan"	to	"f436236e-0e55-4977-a908-e552ee32db9c",
        "Kabupaten Bandung"	to	"86fd7896-a3a1-49c0-abb9-9210ea92440a",
        "Kabupaten Bandung Barat"	to	"9823e08c-1a3b-47be-8fb7-dd4db674a3c2",
        "Kabupaten Bekasi"	to	"91e6895b-64ae-40d9-834f-5727f13c4d3f",
        "Kabupaten Bogor"	to	"da780a0e-f36a-4387-bdeb-777a8ee89981",
        "Kabupaten Ciamis"	to	"f4f260b4-6614-4d9f-ae86-485056eb51ca",
        "Kabupaten Cianjur"	to	"42acedc6-7f77-4dc6-9c6b-53aac890cc25",
        "Kabupaten Cirebon"	to	"a0a5ccfd-3881-4cdb-9670-c5f6ae587d23",
        "Kabupaten Garut"	to	"7b116101-cf75-4bda-a389-ceec05890c81",
        "Kabupaten Indramayu"	to	"9ed3c05a-0939-42fd-9ef5-3cba92b9e616",
        "Kabupaten Karawang"	to	"859007c4-68ed-40a2-82d2-4fd9ea25abc4",
        "Kabupaten Kuningan"	to	"9767cab5-ed48-41df-a2c3-b39bbe63e9d4",
        "Kabupaten Majalengka"	to	"003024f2-a8c3-4d11-9430-395e636d06cf",
        "Kabupaten Pangandaran"	to	"969d1792-8b5d-4ce8-8428-4e90d30f41dd",
        "Kabupaten Purwakarta"	to	"4e4108ea-aaf3-4325-b69c-9a7cb8887591",
        "Kabupaten Subang"	to	"41728a04-c524-4bb8-81db-d8b2fede7f3f",
        "Kabupaten Sukabumi"	to	"2f5387b8-79f8-478e-a0f5-4ea0f8efee16",
        "Kabupaten Sumedang"	to	"5c02bd7f-5aeb-4f1a-a709-8334c1abe931",
        "Kabupaten Tasikmalaya"	to	"31e7d8b4-0575-4858-8f4e-725b33f34bb1",
        "Kota Bandung"	to	"8a63d41a-ac62-4d86-8e85-9d23e901db22",
        "Kota Banjar"	to	"e74b0a2e-7456-46a4-ae13-30ccc74f6d38",
        "Kota Bekasi"	to	"4117a9e7-4491-4695-9caf-54ae5c9255e2",
        "Kota Bogor"	to	"8bef58bf-3114-4048-87c6-419596d40329",
        "Kota Cimahi"	to	"98809ca2-3bde-4475-906b-2c7e9eaea713",
        "Kota Cirebon"	to	"7b75a5c9-df16-4f64-a630-6627814a6375",
        "Kota Depok"	to	"6c4b8bc0-a613-4a2a-bfd1-457bb6a31e74",
        "Kota Sukabumi"	to	"e437f518-ed7d-4fbd-9891-d0c7e8629c62",
        "Kota Tasikmalaya"	to	"bf41e954-1e49-4e47-b4b7-f0ec328c0d72",
        "Kabupaten Administrasi Kepulauan Seribu"	to	"18d965f0-62a1-447e-acb3-1834397cefde",
        "Kota Administrasi Jakarta Barat"	to	"7cc2932d-3fc7-4d09-a0c8-1cf8153c857b",
        "Kota Administrasi Jakarta Pusat"	to	"41e646a5-f563-47a8-ba9b-381facc6a1d3",
        "Kota Administrasi Jakarta Selatan"	to	"51f1c4ec-35f2-4540-b120-459099bbcd68",
        "Kediri"	to	"309d4cd2-6de2-4860-8e75-6d198ae58c5c",
        "Kota Administrasi Jakarta Timur"	to	"6e39da6a-0a93-47ae-88b1-321ef55fd5b1",
        "Kota Administrasi Jakarta Utara"	to	"c6c476e2-d61e-4f84-8a94-12fa80ebe079",
        "Kabupaten Banjarnegara"	to	"4428a94d-e34d-4314-a20f-10e39c054e86",
        "Kabupaten Banyumas"	to	"63eefd65-69c2-4758-8c1e-f2934dc0154e",
        "Kabupaten Batang"	to	"25f00c0b-4890-456c-b1a4-10ee53f7efdf",
        "Kabupaten Blora"	to	"1780c6b2-34c8-4d4a-9e21-bb30bfa099d0",
        "Kabupaten Boyolali"	to	"6fbc9817-e8d7-44aa-87be-8a92a955e38f",
        "Kabupaten Brebes"	to	"8492a7ea-b70e-45f7-98a3-2ac6aae3eb5c",
        "Kabupaten Cilacap"	to	"575c37c1-3564-4374-a59a-7b147cbdc09e",
        "Kabupaten Demak"	to	"57768f26-7eb8-4c04-ac14-efa163929bc5",
        "Kabupaten Grobogan"	to	"d2bbbc5e-ddae-4422-8304-896d7c1e6722",
        "Kabupaten Jepara"	to	"cec7108e-fc52-4361-ae5f-e4e11c9f14cc",
        "Kabupaten Karanganyar"	to	"f479f124-6b11-44c2-aa71-2414142d9b44",
        "Kabupaten Kebumen"	to	"8370ddc3-e247-4045-aa5e-a0db846f8e7b",
        "Kabupaten Kendal"	to	"879d0fc5-02ac-4245-a64a-0c66861a50e9",
        "Kabupaten Klaten"	to	"816ee2d1-04b5-486e-8b74-a0e873ab9425",
        "Kabupaten Kudus"	to	"14f36dc6-7472-4995-9dee-78864236d7fb",
        "Kabupaten Magelang"	to	"a3865a39-dbf8-4d6c-8901-42d6ae0e1867",
        "Kabupaten Pati"	to	"75d30ee0-66df-43f6-ae3f-190c9d02fd1b",
        "Kabupaten Pekalongan"	to	"2e6231d6-191b-42fd-8ced-c554f71994e4",
        "Kabupaten Pemalang"	to	"5c29b29b-2f5e-4a79-b853-45538de9334f",
        "Kabupaten Purbalingga"	to	"4ecf2966-47db-40dd-b713-e2fd61b36c35",
        "Kabupaten Purworejo"	to	"4bcd3aba-f880-43a8-a0d6-b31c40f87d9e",
        "Kabupaten Rembang"	to	"df908873-213e-4609-a519-85e51e369b6e",
        "Kabupaten Semarang"	to	"6122d475-2d54-4fbb-aa23-5aba19db5210",
        "Kabupaten Sragen"	to	"809ff876-1541-4361-b63a-74a76261f2c5",
        "Kabupaten Sukoharjo"	to	"0c5468ac-e65a-49d7-a16c-6fbce75ccc89",
        "Kabupaten Tegal"	to	"014d2c4c-3a36-4745-96ef-85e733bd1f52",
        "Kabupaten Temanggung"	to	"1781c6b5-7754-440b-b04e-64201d8d2cb1",
        "Kabupaten Wonogiri"	to	"812948de-711b-4235-bcda-2199af011750",
        "Kabupaten Wonosobo"	to	"efd0e69b-c56c-4f45-aedc-a3d0bc1aac46",
        "Kota Magelang"	to	"c00ad0d6-f273-4a02-871c-f6e2f9e321d1",
        "Kota Pekalongan"	to	"5b75adfa-2946-4630-bfc4-7fd484bac9e4",
        "Kota Salatiga"	to	"7fa2f168-7c15-4e0f-9c8c-8df0b363fc04",
        "Kota Semarang"	to	"22e6853d-ad95-45b0-a03b-c9c543629766",
        "Kota Surakarta"	to	"441b83c9-c965-4d17-8167-f76963fd6247",
        "Kota Tegal"	to	"d7b9e488-d565-42af-ac98-9e5eac245f55",
        "Kabupaten Bantul"	to	"cfccbfd2-930a-4706-9ac9-34657774a174",
        "Kabupaten Gunungkidul"	to	"fbfa962f-cac1-4a68-8b09-990e154c2bab",
        "Kabupaten Kulon Progo"	to	"de95f49c-1c8e-4f70-8d49-51dd2f612db0",
        "Kabupaten Sleman"	to	"3a922251-bdbc-4082-9fec-3468c5f897c6",
        "Kota Yogyakarta"	to	"08843e64-31c5-47f7-8d26-ba1bd743b61d",
        "Bangkalan"	to	"ee1841fb-6c5b-4c97-bafd-0d914d4e5a2e",
        "Banyuwangi"	to	"db05c4d1-4ae4-4f50-b0bd-60338e4b69c6",
        "Blitar"	to	"a450338e-9825-4cbf-93b2-5e5ce3600235",
        "Bojonegoro"	to	"d5b3df92-2097-4e7b-8cf2-d7e5e4560d6b",
        "Bondowoso"	to	"7ed63f56-0ff8-4f89-b984-4f7944deab07",
        "Gresik"	to	"316e94e5-c7cb-47c1-a864-98fcd77d7037",
        "Jember"	to	"a383ece9-7dad-4884-8859-a7f89920d349",
        "Jombang"	to	"b00a3216-e19d-4eec-86b0-1f9d751e74a1",
        "Kediri"	to	"6bd5eab7-ac1d-495b-86e9-57a4b2836c76",
        "Lamongan"	to	"52889678-4c52-4581-a011-c58fa41d8cca",
        "Lumajang"	to	"7f6be07c-b70b-4899-8913-87bef076a747",
        "Madiun"	to	"1a9c15e1-c102-41bd-a410-4d810b8a953f",
        "Magetan"	to	"cf0bacc6-69ab-4d7c-914c-5d72311ef8ce",
        "Malang"	to	"512d0557-374c-4b92-9922-9579343693fe",
        "Mojokerto"	to	"9966c6e5-beb9-43d4-bbfa-634336dd2279",
        "Nganjuk"	to	"f8e8386e-db94-46f3-b339-18187e298a8f",
        "Ngawi"	to	"67bbfe49-d0f1-49b0-bd95-9cfde1f3f088",
        "Pacitan"	to	"9cd5c11b-21c6-4783-bbf9-907e47280118",
        "Pamekasan"	to	"1b19fbc5-ea5e-4a3d-96a6-ae3e5dc21085",
        "Pasuruan"	to	"0e75a7e6-87a9-4e26-b632-af8d7ff74edf",
        "Ponorogo"	to	"5ce1de33-c731-4707-bc59-593a3e75fead",
        "Probolinggo"	to	"2dcdb87a-ea7c-4dc0-ab0b-4fdd523366e6",
        "Sampang"	to	"975ffbea-4e8d-41ff-a413-b10d473c3ee1",
        "Sidoarjo"	to	"c526f099-781f-491d-a61d-46d58be50da5",
        "Situbondo"	to	"455c5a70-403d-4a4e-912a-7df62c45b40d",
        "Sumenep"	to	"e64f1471-eaf0-47db-97a9-2e8ad562816b",
        "Trenggalek"	to	"6431431d-e5c7-46d6-9fe0-676fcc0e5ae6",
        "Tuban"	to	"4a3e25fb-49b7-4b98-be45-d2a3d4710904",
        "Tulungagung"	to	"8f3d9d85-b177-4094-bf2a-a0ad11a36106",
        "Kota Batu"	to	"63229e61-664e-41e2-9b8d-0f46141109d8",
        "Kota Blitar"	to	"4eaebbb2-041a-44f3-b209-156ab0d50cad",
        "Kota Kediri"	to	"77d11bad-7aca-4fcd-ab07-9d3f53a39666",
        "Kota Madiun"	to	"7f7c9578-4c77-4d6f-b5c9-26ba8c1733e0",
        "Kota Malang"	to	"3c0b0145-78b4-46bb-945c-dd42f5131f9d",
        "Kota Mojokerto"	to	"0606e556-566f-4230-967d-31956807564f",
        "Kota Pasuruan"	to	"decbb34f-cddc-45f4-ac9f-7c0dd14d1e63",
        "Kota Probolinggo"	to	"d0d9efe3-c868-4b37-811e-ff1665c71816",
        "Kota Surabaya"	to	"693126f4-5ace-4d1e-88db-be822e51c010",
        "Kabupaten Badung"	to	"91472272-becd-43ae-8b7a-6c219ea486e4",
        "Kabupaten Bangli"	to	"47103f2f-a787-41ef-adb8-6fde56cfc7ff",
        "Kabupaten Buleleng"	to	"f2190e0f-4de8-484e-a3d5-5f06d7a54c32",
        "Kabupaten Gianyar"	to	"054506ae-150d-4f9c-8e66-93bb066df35f",
        "Kabupaten Jembrana"	to	"469192fa-13c2-42fc-97dd-77e163d183ce",
        "Kabupaten Karangasem"	to	"39c91d9c-c799-4d8a-885f-bd408d361d2c",
        "Kabupaten Klungkung"	to	"70c5c934-2aa2-440f-963c-88313e70f4a4",
        "Kabupaten Tabanan"	to	"823b3633-a2f6-4e18-bbc4-3c5a5364a393",
        "Kota Denpasar"	to	"346c8c75-5ead-444d-9944-a509a4d329cd",
        "Kabupaten Bima"	to	"a1bb44fa-afc8-4c90-94b7-b8e24462a06c",
        "Kabupaten Dompu"	to	"cc10d61c-f3e9-477e-891a-e28d2e201eec",
        "Kabupaten Lombok Barat"	to	"0704e7b7-92c8-4172-9cae-17aa22171318",
        "Kabupaten Lombok Tengah"	to	"ba8a7ffd-ced0-427b-ac36-627bb72f3bdb",
        "Kabupaten Lombok Timur"	to	"54ac0e03-c584-45eb-8cc1-c6c258e0e674",
        "Kabupaten Lombok Utara"	to	"ecb34756-636b-4b5a-94a5-e623c7f2ccc2",
        "Kabupaten Sumbawa"	to	"a1c317de-431a-4497-9097-12c65a65602c",
        "Kabupaten Sumbawa Barat"	to	"46e145a0-595f-4648-948e-032dcc3debe2",
        "Kota Bima"	to	"2153ee62-2b7c-443b-879a-4866bfa25f10",
        "Kota Mataram"	to	"f0c78bfb-337d-4d8d-a61a-8767f31d6070",
        "Kabupaten Alor"	to	"9ffae250-dadd-4ab8-b156-fe8ca0f25101",
        "Kabupaten Belu"	to	"47d909b7-a81a-4634-93be-52e9441c9286",
        "Kabupaten Ende"	to	"d4b6b6be-fda4-4098-810c-56e0dcb24b0c",
        "Kabupaten Flores Timur"	to	"09051cf9-0523-41d8-8e9c-5f5e01f85ef7",
        "Kabupaten Kupang"	to	"fbf65b85-d98c-42ea-9724-c69a5b82ea13",
        "Kabupaten Lembata"	to	"7c4c30f6-8284-4a91-aaa8-9152e92d7a6a",
        "Kabupaten Malaka"	to	"cc23632c-44f5-44fb-bdc6-cc07c1525f8b",
        "Kabupaten Manggarai"	to	"1561776f-c831-4f4c-ac30-ce3e28c8db4b",
        "Kabupaten Manggarai Barat"	to	"e4a64612-6242-436e-91d8-b9b494b4835f",
        "Kabupaten Manggarai Timur"	to	"e7ffa516-5896-4769-a557-474f26ac6313",
        "Kabupaten Nagekeo"	to	"64ec3bd5-455b-4dd4-b930-0e412a025a6a",
        "Kabupaten Ngada"	to	"8123b1fc-0519-4819-91f6-6c545ba26943",
        "Kabupaten Rote Ndao"	to	"12e2ace1-53f9-4893-b6d7-12951e0bbdb3",
        "Kabupaten Sabu Raijua"	to	"b880da07-8299-4012-b7cf-fac5c7ed575e",
        "Kabupaten Sikka"	to	"b7ae19bd-0919-4540-9aa0-25d9a5b14c9f",
        "Kabupaten Sumba Barat"	to	"b88dc38f-a6ef-4dd3-bddd-8923972141ed",
        "Kabupaten Sumba Barat Daya"	to	"150449e4-5096-4c01-aa2d-b706b99543d3",
        "Kabupaten Sumba Tengah"	to	"f3359c04-dd26-4005-b2b3-bec8c00c225d",
        "Kabupaten Sumba Timur"	to	"9fc5b1f9-e884-4ea1-aa3a-8280fd7c7e0d",
        "Kabupaten Timor Tengah Selatan"	to	"37a53732-c5f8-4427-9dd1-cf134f1b4acf",
        "Kabupaten Timor Tengah Utara"	to	"619a2265-abbe-40f3-acc6-86adbeea9341",
        "Kota Kupang"	to	"8e45c48d-3e61-4aec-b5a8-5532d28625ee",
        "Kabupaten Bengkayang"	to	"5a3bae15-07c2-48ee-862d-2962f1271d35",
        "Kabupaten Kapuas Hulu"	to	"b96b9ae9-9325-4c22-97a1-3ec390dc6f40",
        "Kabupaten Kayong Utara"	to	"a54483a8-31c8-426a-bd5f-a61f42767565",
        "Kabupaten Ketapang"	to	"bdd4a0b3-e36e-48f4-8da0-54c408901c63",
        "Kabupaten Kubu Raya"	to	"843ed697-f104-4215-994e-daa55f034647",
        "Kabupaten Landak"	to	"c79d24f1-25f8-46a3-80c4-ef41195175f0",
        "Kabupaten Melawi"	to	"579007e5-1241-4ff8-b179-a2c6b918ed6a",
        "Kabupaten Mempawah"	to	"8997bcfa-b600-45a6-b0eb-7865e21e9006",
        "Kabupaten Sambas"	to	"505473b9-aecc-4ea1-aa24-c393e65e4d73",
        "Kabupaten Sanggau"	to	"cee0381e-1f3a-4360-bd18-bb7edc6623ed",
        "Kabupaten Sekadau"	to	"7bf909ee-67a2-4cff-b6a4-ac07caed69d5",
        "Kabupaten Sintang"	to	"043237f9-38f2-435d-93e3-6bfbfddc24fb",
        "Kota Pontianak"	to	"776cd625-eaf4-409d-999a-e91a790d6e7b",
        "Kota Singkawang"	to	"08a2595e-7be6-445d-a0d7-0260d5db66ba",
        "Kabupaten Balangan"	to	"c4fda414-1c47-46d7-a581-4a0c7db8fedc",
        "Kabupaten Banjar"	to	"b798434a-a36a-4786-bdba-36acd4453b95",
        "Kabupaten Barito Kuala"	to	"7ba2fd54-e8aa-403f-b899-c047ce8fb9ec",
        "Kabupaten Hulu Sungai Selatan"	to	"75474a4e-a245-4780-8590-94573af93518",
        "Kabupaten Hulu Sungai Tengah"	to	"6cb8b71f-3b5f-471b-9dd7-9c43d8f05ff2",
        "Kabupaten Hulu Sungai Utara"	to	"f48db7cf-8aa6-4ed9-bdfb-c6b4fd6da072",
        "Kabupaten Kotabaru"	to	"8fb14509-fef6-4f13-a469-c379307c4c1c",
        "Kabupaten Tabalong"	to	"23122d96-82af-4f22-98a1-d6821da9704e",
        "Kabupaten Tanah Bumbu"	to	"c4589f28-c15c-4579-9976-8d787db50594",
        "Kabupaten Tanah Laut"	to	"b1075da5-cf51-4a99-a21d-0fe8369262a0",
        "Kabupaten Tapin"	to	"cfde8314-240c-4e97-a8dd-1f5ecfa1e5de",
        "Kota Banjarbaru"	to	"716c46d1-57a4-4a72-aa5c-3db49d4ef2d8",
        "Kota Banjarmasin"	to	"72966ded-852f-4075-bf94-05bb4219cbe4",
        "Kabupaten Barito Selatan"	to	"4e9f0c09-a987-4b07-8c6b-e93ea7290dbd",
        "Kabupaten Barito Timur"	to	"0412f993-1c2c-4f15-827c-1f5292be2d4c",
        "Kabupaten Barito Utara"	to	"018c3b8f-cf9f-4108-94d6-6361657ba1bb",
        "Kabupaten Gunung Mas"	to	"e826c78b-a250-4f87-a0a0-ce31b5f0b2aa",
        "Kabupaten Kapuas"	to	"f40fa68d-d7af-44f8-b2d2-80ce8bbe2df6",
        "Kabupaten Katingan"	to	"8f982877-b8fa-40f8-9193-742cac435e6c",
        "Kabupaten Kotawaringin Barat"	to	"f75b18e4-f5b8-45be-80d5-9f5719c2aa8f",
        "Kabupaten Kotawaringin Timur"	to	"b8a9ddbd-c5a0-4306-bba2-8195fd883e39",
        "Kabupaten Lamandau"	to	"4c1a5e9a-49bf-458f-8022-6543a7deee8c",
        "Kabupaten Murung Raya"	to	"da5ed7a1-cc84-4b73-a2e2-e4723fedd007",
        "Kabupaten Pulang Pisau"	to	"d26f573e-0577-4640-8e85-47bdc380b744",
        "Kabupaten Sukamara"	to	"33f48ae3-6e62-4552-a578-2730f91530ad",
        "Kabupaten Seruyan"	to	"ba1ba0ae-7edd-45d6-9ae4-0f800089ec59",
        "Kota Palangka Raya"	to	"e3d26bf8-2901-42a0-b2b1-66ccd1f5a673",
        "Kabupaten Berau"	to	"2a2c9eeb-a132-486e-af00-8f5dca26582b",
        "Kabupaten Kutai Barat"	to	"299ccb1f-8a32-474a-a999-90b14213de53",
        "Kabupaten Kutai Kartanegara"	to	"12b42f96-9f21-47ab-9b2e-b7c960f97b07",
        "Kabupaten Kutai Timur"	to	"71b3d9c3-dc9e-4871-a2d6-91d76e246a4f",
        "Kabupaten Mahakam Ulu"	to	"3865d12f-d4d2-4a74-be04-b2e7ad405691",
        "Kabupaten Paser"	to	"90638557-9ef8-456e-94e4-68623bfc0f6e",
        "Kabupaten Penajam Paser Utara"	to	"03a515bb-27b7-4ca9-8f98-2287e4fb020a",
        "Kota Balikpapan"	to	"69369f1d-ccd2-4f80-8c2a-ac523188dc2a",
        "Kota Bontang"	to	"8da0698f-77cd-432a-9b0e-664a44aa1cdb",
        "Kota Samarinda"	to	"988c578f-2877-4d9e-80ed-a11962b2620a",
        "Kabupaten Bulungan"	to	"f604894f-5e3e-4bac-844a-f9f533278554",
        "Kabupaten Malinau"	to	"d36c59ac-d709-4900-a790-f565882d8479",
        "Kabupaten Nunukan"	to	"e1617387-bcec-4397-854a-ea47fff92798",
        "Kabupaten Tana Tidung"	to	"cd6e5ea4-fd64-4131-b494-741ce70fda8d",
        "Kota Tarakan"	to	"2e14924e-19fd-4f8d-8d7c-c5a632f29292",
        "Kabupaten Bolaang Mongondow"	to	"a51ba677-de66-49ab-a4c6-e5312836fcc4",
        "Kabupaten Bolaang Mongondow Selatan"	to	"c6522c7d-4306-4ab4-8765-209078ef625a",
        "Kabupaten Bolaang Mongondow Timur"	to	"58f1f83e-3a01-420a-ba19-6f77b02f3675",
        "Kabupaten Bolaang Mongondow Utara"	to	"fd592f0e-323b-428a-a769-888113cb0b15",
        "Kabupaten Kepulauan Sangihe"	to	"b7299e19-e3a0-4e74-982f-c7204a183d15",
        "Kabupaten Kepulauan Siau Tagulandang Biaro"	to	"45bf35a2-28cb-443d-b1c8-4a47a39350a5",
        "Kabupaten Kepulauan Talaud"	to	"837ac3bc-d380-44cc-9d82-c8f97401b8a5",
        "Kabupaten Minahasa"	to	"c61eed0c-34d5-4299-9467-a229d1f5728f",
        "Kabupaten Minahasa Selatan"	to	"057fd092-c876-4c4e-bb49-1b64583b8e17",
        "Kabupaten Minahasa Tenggara"	to	"848c7119-aa5f-4d27-94d6-cbb97f5bead9",
        "Kabupaten Minahasa Utara"	to	"96d21397-8abc-4db4-9df5-a1781975db40",
        "Kota Bitung"	to	"beb98e6f-c6a7-4407-8692-6efd9736bc43",
        "Kota Kotamobagu"	to	"38a45fc4-f51a-4e6f-ae71-79ca8428eb14",
        "Kota Manado"	to	"60aeeea5-0ddd-4883-91e0-43e207dcd37a",
        "Kota Tomohon"	to	"27001604-210a-4336-96bf-14d4c9154d6d",
        "Kabupaten Boalemo"	to	"befb586f-da7c-4b38-aba1-963c43c993d1",
        "Kabupaten Bone Bolango"	to	"e06a9270-4035-4141-9de4-9f89e4a11cc4",
        "Kabupaten Gorontalo"	to	"0a1bc01e-6c1c-4e70-9ae5-76316cad3508",
        "Kabupaten Gorontalo Utara"	to	"d5b992f8-34e5-4f1d-b6a0-5827e4da8016",
        "Kabupaten Pohuwato"	to	"9b993b8f-4b94-4fb9-b607-4b8046de2562",
        "Kota Gorontalo"	to	"089ff8cb-fe23-4cbc-a4c8-14073efe1101",
        "Kabupaten Banggai"	to	"ecd82c64-aa57-4897-807f-0e14dad80610",
        "Kabupaten Banggai Kepulauan"	to	"e86156c4-3e29-4308-b4a1-1a861a09b304",
        "Kabupaten Banggai Laut"	to	"b6bb0825-89ec-475d-afc0-18e7968b7917",
        "Kabupaten Buol"	to	"006168f2-74e8-4ff1-97af-c1de19ddd46d",
        "Kabupaten Donggala"	to	"72f90150-4c00-46aa-b708-dff18745b036",
        "Kabupaten Morowali"	to	"82d0ae77-67af-4a95-8209-47982c879e15",
        "Kabupaten Morowali Utara"	to	"fdbcc4df-0cf4-4167-a1ba-b657be8062a6",
        "Kabupaten Parigi Moutong"	to	"606ad485-cab0-4d4c-bd5f-c12e8a93dd41",
        "Kabupaten Poso"	to	"27672386-81ef-4181-9409-e241c15d6408",
        "Kabupaten Sigi"	to	"626c503d-cb30-4258-abcb-7feacd1a2bc8",
        "Kabupaten Tojo Una-Una"	to	"44461ed1-f60b-4952-bad4-2dc1dff95ba4",
        "Kabupaten Tolitoli"	to	"1fba7e8d-802c-428b-8c2b-81a501175f7c",
        "Kota Palu"	to	"5e5c587c-bd43-4b07-b208-f53c82377000",
        "Kabupaten Majene"	to	"18399a3d-2578-4ac7-b363-88187bd97965",
        "Kabupaten Mamasa"	to	"3c3f4d2a-6bf1-4ace-956d-0f3861d78eaf",
        "Kabupaten Mamuju"	to	"2a7adbef-08b5-4d5d-85dd-310279a1a829",
        "Kabupaten Mamuju Tengah"	to	"ef83714f-8883-45eb-8303-43cfc1f760ad",
        "Kabupaten Pasangkayu"	to	"3527dc95-8288-463a-9889-b36ac907eeee",
        "Kabupaten Polewali Mandar"	to	"1103b416-c26e-44c6-aa42-3a47a94c973e",
        "Kabupaten Bantaeng"	to	"abddf9ba-382f-48fd-89e9-0f02694ab788",
        "Kabupaten Barru"	to	"057b44e7-d797-421a-8dcf-cb44b8c57c24",
        "Kabupaten Bone"	to	"cb8bbbec-f27a-4f8e-9a82-702f6cf81c99",
        "Kabupaten Bulukumba"	to	"9b56e1f0-430e-45be-a975-d92736a1407d",
        "Kabupaten Enrekang"	to	"33223afe-29af-4485-866a-bb853da1cd2a",
        "Kabupaten Gowa"	to	"5b4cff86-dd13-4684-934e-9618502c0109",
        "Kabupaten Jeneponto"	to	"34d7c6ae-c42d-46ef-a5f6-75bfbdec8f26",
        "Kabupaten Kepulauan Selayar"	to	"93e9ba2d-b54d-4c47-9571-190d534a8ac2",
        "Kabupaten Luwu"	to	"981c79a8-222b-4328-9766-61692411785f",
        "Kabupaten Luwu Timur"	to	"e8e0e398-faee-4c77-8189-07443ad2a7bb",
        "Kabupaten Luwu Utara"	to	"f40159e0-4bbb-422d-aad7-cec7fe3204bf",
        "Kabupaten Maros"	to	"ca77051a-7b33-4a9a-bc0b-013687380999",
        "Kabupaten Pangkajene dan Kepulauan"	to	"a227e14b-69da-4ba7-b49d-b1f460331f0e",
        "Kabupaten Pinrang"	to	"1230f28a-2d50-4dfa-b625-0be333cbe4b1",
        "Kabupaten Sidenreng Rappang"	to	"dffb758a-c7ce-4259-a02b-9c2d4d64a9ae",
        "Kabupaten Sinjai"	to	"5d2d59f0-f79d-4712-be60-229042ecb3f5",
        "Kabupaten Soppeng"	to	"aefbc7de-cb06-44ef-a02b-59264c48e90b",
        "Kabupaten Takalar"	to	"3a5b214f-c923-433a-964f-62c159e98443",
        "Kabupaten Tana Toraja"	to	"21474af7-578b-4a50-9b76-3c556f616ae0",
        "Kabupaten Toraja Utara"	to	"8c308d17-51b0-4ecf-b24e-72fdad360908",
        "Kabupaten Wajo"	to	"5a795299-470e-4679-b8f5-78c338fa6793",
        "Kota Makassar"	to	"4661bf4c-68fa-40e8-ae6e-ad3633aa41ac",
        "Kota Palopo"	to	"a4dd58b0-ab6b-4a94-8c67-26bc52dc0bc1",
        "Kota Parepare"	to	"9368a9a6-d4da-4855-a45b-8b2286f53d2b",
        "Kabupaten Bombana"	to	"b352ecd9-6ca2-4056-8623-c7b3be859a1d",
        "Kabupaten Buton"	to	"9b7be92a-e374-4df4-a07e-1d29697cda79",
        "Kabupaten Buton Selatan"	to	"4625523a-ac90-4d62-9e28-53ca1a62bee8",
        "Kabupaten Buton Tengah"	to	"0069f906-a26c-41cc-bf9c-37aafa61dd64",
        "Kabupaten Buton Utara"	to	"704c5496-3d01-4418-853c-71488b10abcc",
        "Kabupaten Kolaka"	to	"b3fd52ec-7234-4eb4-80ef-4d137262bc56",
        "Kabupaten Kolaka Timur"	to	"5a8d33de-ef78-48da-a5e8-bdcdc03be755",
        "Kabupaten Kolaka Utara"	to	"82338f01-60aa-46e1-a554-95fcb9bf2c78",
        "Kabupaten Konawe"	to	"285c00cd-338c-438a-873b-cac04be1f763",
        "Kabupaten Konawe Kepulauan"	to	"b7628d14-4f2d-43e1-8147-cafd8637097d",
        "Kabupaten Konawe Selatan"	to	"cc48e935-cee5-4ecd-8066-7ffea048709c",
        "Kabupaten Konawe Utara"	to	"d7de078a-c89f-46d8-afa4-a6c21e1d2dae",
        "Kabupaten Muna"	to	"b74fa96b-82c1-47f2-96b9-619773095e09",
        "Kabupaten Muna Barat"	to	"14e044f0-3052-4a0b-919a-cbee6e11e0c4",
        "Kabupaten Wakatobi"	to	"6c473f8a-39d9-4859-8cb7-6ad3a17f8465",
        "Kota Baubau"	to	"0caef9f4-483c-4ef4-8748-13764d0f0bda",
        "Kota Kendari"	to	"395bc839-86a9-4b92-93fb-083279b56143",
        "Kabupaten Buru"	to	"e7380fd5-1b51-4125-b879-6d3191cbc382",
        "Kabupaten Buru Selatan"	to	"953568f6-bf37-46ab-b68d-e6c89f1be898",
        "Kabupaten Kepulauan Aru"	to	"fc29b8eb-7928-4d78-9e56-7df3b7d0e318",
        "Kabupaten Kepulauan Tanimbar"	to	"3eeff7d0-1954-47cd-808e-86c56553af6c",
        "Kabupaten Maluku Barat Daya"	to	"5bf7415e-8312-4ff4-aac0-130bc0f5581b",
        "Kabupaten Maluku Tengah"	to	"3d7850c3-6b66-4281-8b2c-045172da9ae8",
        "Kabupaten Maluku Tenggara"	to	"54abce84-2cd8-42b3-9df7-ac4d24e297bf",
        "Kabupaten Seram Bagian Barat"	to	"f2a0bf24-62b5-42f6-a5d6-e807f9c3d934",
        "Kabupaten Seram Bagian Timur"	to	"a632aecb-80bf-4663-95c3-03f6b6cb93c6",
        "Kota Ambon"	to	"5fe53d98-adf4-457d-9070-ceb4c19c7527",
        "Kota Tual"	to	"45c4a2f1-4302-47e4-b22e-34985c269c7c",
        "Kabupaten Halmahera Barat"	to	"24ffe72a-8860-465f-8957-31ead40ec6d9",
        "Kabupaten Halmahera Tengah"	to	"e513eaf8-a921-4064-a313-ce0eb9d7b6d4",
        "Kabupaten Halmahera Timur"	to	"39391500-9cbc-44d9-bbe5-c3f5e3e9d58a",
        "Kabupaten Halmahera Selatan"	to	"89b5014c-dc23-47ce-bf93-31eb3cbc443b",
        "Kabupaten Halmahera Utara"	to	"93265a69-a4ca-41e3-9c2d-41dfa586cde4",
        "Kabupaten Kepulauan Sula"	to	"ccbfa5ff-81b7-46e3-8f72-1483e834f556",
        "Kabupaten Pulau Morotai"	to	"edda3f44-a74d-4e33-9ef6-9534c5500e9f",
        "Kabupaten Pulau Taliabu"	to	"e4f20fd1-bd43-4e78-a2d1-6a6b3a8eccb5",
        "Kota Ternate"	to	"afc75017-ae62-414e-9921-19eeb365df36",
        "Kota Tidore Kepulauan"	to	"6c6d59f7-7cd3-48cd-8db4-f345b02df5d8",
        "Kabupaten Biak Numfor"	to	"97c2c827-82bb-479b-aa9b-36d713be22d2",
        "Kabupaten Jayapura"	to	"462acf3b-b514-49f6-9304-973a3a0bd555",
        "Kabupaten Keerom"	to	"c0cdbfab-ba67-4366-bed3-b55613d992ff",
        "Kabupaten Kepulauan Yapen"	to	"4ff22225-7a63-4787-9cb4-d395d3431980",
        "Kabupaten Mamberamo Raya"	to	"1ea3b697-8575-4fe5-ba96-20e17f328ea9",
        "Kabupaten Sarmi"	to	"9793b880-bef4-4158-bc56-fcd983faa99c",
        "Kabupaten Supiori"	to	"e3e64d32-7338-46e9-a54b-50a2ead5c3fd",
        "Kabupaten Waropen"	to	"388d93ee-1ab0-433d-9bf4-3595847a68e8",
        "Kota Jayapura"	to	"0122a467-81de-4190-8c30-bdb717a0bd81",
        "Kabupaten Fakfak"	to	"f60f729c-938b-40f0-94ab-8195076d8ecd",
        "Kabupaten Kaimana"	to	"c945aaf9-af32-465d-95c9-5f4446074127",
        "Kabupaten Manokwari"	to	"85f5b823-5fc1-45fc-933b-535c2965a257",
        "Kabupaten Manokwari Selatan"	to	"dda609fb-92d3-4dd2-99ea-084e09359c3a",
        "Kabupaten Pegunungan Arfak"	to	"f9707791-404d-48b3-9fd1-0b300d701d19",
        "Kabupaten Teluk Bintuni"	to	"360cb59e-7fba-48c3-821f-353d28bcc16c",
        "Kabupaten Teluk Wondama"	to	"87e9af46-1bbb-4953-af91-d2251f7d4986",
        "Kabupaten Jayawijaya"	to	"2cd1d28a-ca07-4751-818a-2a77f463be5c",
        "Kabupaten Lanny Jaya"	to	"a52faa1e-e098-46bd-a453-03b4261f9965",
        "Kabupaten Mamberamo Tengah"	to	"d75e7799-6c7f-4346-b443-5b77e5664077",
        "Kabupaten Nduga"	to	"06be6b4a-d109-4bf8-9a8d-d8371dacfbbe",
        "Kabupaten Pegunungan Bintang"	to	"f861a837-e0bb-46ee-b2dd-51e1eae2f27f",
        "Kabupaten Tolikara"	to	"d0355582-ec4e-430d-9b13-64b80ad17a46",
        "Kabupaten Yalimo"	to	"2d452121-5468-4543-ba5a-c5248339abb2",
        "Kabupaten Yahukimo"	to	"8d81b26f-eaa1-4688-9973-8aac76455c6e",
        "Kabupaten Asmat"	to	"3c8b547d-b463-472a-bdd7-17f741d414d0",
        "Kabupaten Boven Digoel"	to	"7f6bd6dc-db26-41d6-adb5-133adaff8c56",
        "Kabupaten Mappi"	to	"22a71544-1957-43b4-821f-2376417458b7"

    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            userRepository = Injection.provideUserRepository(applicationContext)
        }

        val name = intent.getStringExtra("name")
        val phone = intent.getStringExtra("phone")
        val city = intent.getStringExtra("city")

        binding.edRegisterName.setText(name)
        binding.autoCompleteTextView.setText(city)

        val ccp = binding.countryCodeHolder
        val phoneNumberEditText = binding.edRegisterPhone

        phone?.let {
            val phoneNumberUtil = PhoneNumberUtil.getInstance()
            val numberProto = phoneNumberUtil.parse(it, "")
            val countryCode = numberProto.countryCode
            val nationalNumber = numberProto.nationalNumber.toString()

            ccp.setCountryForPhoneCode(countryCode)
            phoneNumberEditText.setText(nationalNumber)
        }

        setupView()
        setupAction()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        val toolbar = findViewById<Toolbar>(R.id.toolbar4)
        toolbar.title = ""

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupAction() {

        val cities = resources.getStringArray(R.array.city_array)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, cities)
        val autocompleteTV = binding.autoCompleteTextView
        autocompleteTV.setAdapter(arrayAdapter)

        autocompleteTV.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateInput(autocompleteTV, cities)
            }
        }

        autocompleteTV.setOnDismissListener {
            validateInput(autocompleteTV, cities)
        }

        autocompleteTV.setOnEditorActionListener { _, _, _ ->
            validateInput(autocompleteTV, cities)
            false
        }


        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()

            val ccp = binding.countryCodeHolder
            val phonenumbertext = binding.edRegisterPhone.text.toString()
            val phonenumber = binding.edRegisterPhone
            ccp.registerCarrierNumberEditText(phonenumber)
            val fullphonenumber = "+" + ccp.fullNumber

            val enteredText = binding.autoCompleteTextView.text.toString()
            val selectedCityId = cityIdMap[enteredText].toString()


            if (checkInput(name, phonenumbertext, enteredText)) {
                binding.progressBar.visibility = View.VISIBLE
                lifecycleScope.launch {
                    try {
                        val response = userRepository.updateUser(name, fullphonenumber, selectedCityId)
                        val message = response.message
                        if (!response.isSuccess) {
                            showAlertDialog(
                                title = "Oh no!",
                                errorMessage = message,
                                type = DialogType.ERROR,
                                icon = R.drawable.baseline_error_outline_24,
                                doAction = {}

                            )
                        } else {
                            showAlertDialog(
                                title = "Hooray!",
                                errorMessage = "Your profile has been updated.",
                                type = DialogType.SUCCESS,
                                icon = R.drawable.outline_how_to_reg_24,
                                doAction = {
                                    navigateToProfile()
                                }
                            )
                        }
                    } catch (e: HttpException) {
                        val jsonInString = e.response()?.errorBody()?.string()
                        val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                        val errorMessage = when (val message = errorBody.message) {
                            is String -> message
                            is List<*> -> message.joinToString(", ")
                            else -> "An unexpected error occurred."
                        }
                        showAlertDialog(
                            title = "Oh no!",
                            errorMessage = errorMessage,
                            type = DialogType.ERROR,
                            icon = R.drawable.baseline_error_outline_24,
                            doAction = {}
                        )
                    } finally {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }

    }

    private fun navigateToProfile() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("fragment", "profile")
        }
        startActivity(intent)
    }


    private fun checkInput(name: String, phonenumber: String, city: String): Boolean {

        var isValid = true
        if (name.isEmpty()) {
            binding.edRegisterName.error =  getString(R.string.invalid_name)
            isValid = false
        }
        if (phonenumber.isEmpty()) {
            binding.edRegisterPhone.error =  getString(R.string.invalid_number)
            isValid = false
        }
        if (city.isEmpty()) {
            binding.autoCompleteTextView.error =  getString(R.string.invalid_city)
            isValid = false
        }
        return isValid
    }

    private fun validateInput(autoCompleteTextView: AutoCompleteTextView, validOptions: Array<String>) {
        val enteredText = autoCompleteTextView.text.toString()
        if (validOptions.contains(enteredText)) {
            Log.d("SelectedCity", "Selected city: $enteredText")
        } else {
            autoCompleteTextView.text = null
            Log.d("InvalidCity", "Entered city is not valid: $enteredText")
            Toast.makeText(this, "Invalid city. Please select a valid city.", Toast.LENGTH_SHORT).show()
        }
    }

    enum class DialogType {
        ERROR,
        SUCCESS
    }

    private fun showAlertDialog(
        title: String,
        errorMessage: String,
        icon: Int,
        type: DialogType,
        doAction: () -> Unit
    ) {
        val builder = AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(errorMessage)
            setIcon(icon)
            setPositiveButton("OK") { _, _ ->
                if (type == DialogType.SUCCESS) {
                    doAction()
                }
            }
        }

        val alertDialog: AlertDialog = builder.create().apply {
            setCancelable(false)
            show()
        }
    }
}