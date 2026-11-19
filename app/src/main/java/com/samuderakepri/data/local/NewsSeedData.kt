package com.samuderakepri.data.local

/**
 * Data cache awal (pre-populated offline cache) yang mencakup seluruh rubrik utama portal
 * Samudera Kepri agar pengguna tetap dapat membaca setiap kategori di area blank spot.
 */
object NewsSeedData {
    fun getInitialCachedArticles(): List<ArticleEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            ArticleEntity(
                id = "seed-esq-gemuruh",
                title = "Didukung Penuh PT Timah Tbk, Pemdes Gemuruh Gelar Seminar ESQ Bangun Karakter Unggul Masyarakat",
                excerpt = "Pemerintah Desa Gemuruh bersama PT Timah Tbk menggelar Seminar ESQ guna memperkuat karakter unggul serta ketahanan sosial warga kepulauan.",
                content = """
                    KARIMUN, SAMUDERAKEPRI — Pemerintah Desa Gemuruh dengan dukungan penuh PT Timah Tbk menyelenggarakan Seminar ESQ Membangun Karakter Unggul Warga Desa Gemuruh yang diikuti tokoh masyarakat, pemuda, dan perangkat desa.

                    Kegiatan pembinaan sumber daya manusia ini menitikberatkan pada penguatan etos kerja, kebersamaan sosial, serta kemandirian ekonomi warga pesisir di Kabupaten Karimun.

                    Perwakilan manajemen PT Timah Tbk menegaskan komitmen perusahaan untuk terus bersinergi bersama pemerintah desa dalam program pemberdayaan masyarakat yang berkelanjutan.
                """.trimIndent(),
                category = "Sosial & Masyarakat",
                region = "Karimun",
                imageUrl = "https://images.unsplash.com/photo-1540575467063-178a50c2df87?auto=format&fit=crop&w=900&q=80",
                articleUrl = "https://www.samuderakepri.co.id",
                author = "Redaksi Samudera Kepri",
                publishedLabel = "15 Nov 2026",
                publishedTimestamp = now - 15 * 60_000L,
                readTimeMinutes = 4,
                isHero = true,
                isInvestigative = false,
                isExclusive = false
            ),
            ArticleEntity(
                id = "seed-kasasi-putik-langir",
                title = "Masa Inzage e-Court Berakhir, Berkas Kasasi Sengketa Jalan Putik-Langir Resmi Teregister di MA",
                excerpt = "Perkara sengketa akses jalan vital Putik-Langir resmi memasuki tahap kasasi di Mahkamah Agung setelah melalui proses panjang di pengadilan tingkat pertama dan banding.",
                content = """
                    TANJUNGPINANG, SAMUDERAKEPRI — Perkara sengketa lahan akses jalan Putik-Langir yang menjadi perhatian masyarakat luas di Kepulauan Riau resmi teregister pada tingkat kasasi di Mahkamah Agung (MA) Republik Indonesia.

                    Tim kuasa hukum bersama perwakilan warga menyatakan seluruh memori kasasi dan dokumen bukti kepemilikan historis telah diserahkan secara lengkap melalui kepaniteraan e-Court.

                    Masyarakat pesisir dan pelaku logistik berharap putusan Mahkamah Agung nantinya dapat menghadirkan kepastian hukum serta menjamin kelancaran distribusi barang antarwilayah kepulauan.

                    Redaksi Samudera Kepri akan terus mengawal jalannya proses hukum ini secara berimbang, akurat, dan transparan sesuai Kode Etik Jurnalistik.
                """.trimIndent(),
                category = "Investigasi",
                region = "Kepri",
                imageUrl = "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?auto=format&fit=crop&w=900&q=80",
                articleUrl = "https://www.samuderakepri.co.id",
                author = "Tim Investigasi Samudera Kepri",
                publishedLabel = "30 Menit Lalu",
                publishedTimestamp = now - 30 * 60_000L,
                readTimeMinutes = 5,
                isHero = false,
                isInvestigative = true,
                isExclusive = false
            ),
            ArticleEntity(
                id = "seed-natuna-apbdp-transparansi",
                title = "Transparansi Pengelolaan Keuangan Daerah Natuna & Dugaan Hambatan Konfirmasi APBD-P",
                excerpt = "Laporan investigasi Gelar Fakta menelusuri dokumen anggaran dan mekanisme konfirmasi keterbukaan informasi publik di wilayah perbatasan Natuna.",
                content = """
                    NATUNA, SAMUDERAKEPRI — Transparansi pengelolaan keuangan daerah serta pelaksanaan Anggaran Pendapatan dan Belanja Daerah Perubahan (APBD-P) di wilayah perbatasan utara Indonesia menjadi sorotan utama liputan Gelar Fakta Samudera Kepri.

                    Berdasarkan penelusuran dokumen publik dan surat konfirmasi resmi redaksi, terdapat sejumlah pos belanja prioritas infrastruktur pesisir yang membutuhkan penjelasan terbuka kepada publik.

                    Redaksi tetap membuka ruang hak jawab seluas-luasnya bagi instansi terkait demi pemenuhan prinsip keberimbangan jurnalistik.
                """.trimIndent(),
                category = "Investigasi",
                region = "Natuna",
                imageUrl = "https://images.unsplash.com/photo-1450133064473-71024230f91b?auto=format&fit=crop&w=900&q=80",
                articleUrl = "https://www.samuderakepri.co.id",
                author = "Redaksi Gelar Fakta",
                publishedLabel = "1 Jam Lalu",
                publishedTimestamp = now - 60 * 60_000L,
                readTimeMinutes = 6,
                isHero = false,
                isInvestigative = true,
                isExclusive = true
            ),
            ArticleEntity(
                id = "seed-politik-iklim-karhutla",
                title = "Temui Presiden Prabowo, DEN Ingatkan Bahaya Iklim Ekstrem 2027 dan Dampak Karhutla di 6 Provinsi",
                excerpt = "Dewan Ekonomi Nasional memaparkan proyeksi risiko iklim ekstrem serta langkah mitigasi lintas kementerian guna menjaga stabilitas pangan dan daerah rawan karhutla.",
                content = """
                    NASIONAL, SAMUDERAKEPRI — Dewan Ekonomi Nasional (DEN) menggelar pertemuan strategis bersama Presiden Prabowo Subianto di Istana Kepresidenan untuk membahas kesiapsiagaan menghadapi potensi iklim ekstrem 2027 dan mitigasi kebakaran hutan serta lahan di enam provinsi prioritas.

                    Dalam rapat koordinasi tersebut, pemerintah menyoroti pentingnya sinergi daerah kepulauan dalam mengantisipasi gangguan cuaca terhadap jalur logistik maritim serta sektor pertanian lokal.

                    Kementerian terkait diminta menyiapkan langkah pencegahan dini dan memperkuat dukungan anggaran penanggulangan bencana di tingkat provinsi dan kabupaten/kota.
                """.trimIndent(),
                category = "Politik & Pemerintahan",
                region = "Nasional",
                imageUrl = "https://images.unsplash.com/photo-1529107386315-e1a2ed48a620?auto=format&fit=crop&w=900&q=80",
                articleUrl = "https://www.samuderakepri.co.id",
                author = "Biro Nasional Samudera Kepri",
                publishedLabel = "2 Jam Lalu",
                publishedTimestamp = now - 2 * 3600_000L,
                readTimeMinutes = 4,
                isHero = false,
                isInvestigative = false,
                isExclusive = false
            ),
            ArticleEntity(
                id = "seed-politik-bakom-komunikasi",
                title = "Panggil Pimpinan Bakom, Presiden Tekankan Pentingnya Komunikasi Pemerintah yang Terbuka dan Dua Arah",
                excerpt = "Pemerintah pusat mendorong penguatan kanal informasi publik yang responsif, transparan, dan berbasis data faktual untuk masyarakat daerah.",
                content = """
                    NASIONAL, SAMUDERAKEPRI — Presiden Prabowo Subianto memanggil jajaran pimpinan Badan Komunikasi Pemerintah untuk mengevaluasi strategi penyampaian informasi kebijakan publik yang lebih terbuka, cepat, dan dua arah.

                    Komunikasi pemerintah yang lugas dinilai krusial agar program pembangunan strategis nasional dapat dipahami langsung oleh masyarakat hingga wilayah terdepan dan kepulauan seperti Kepulauan Riau.
                """.trimIndent(),
                category = "Politik & Pemerintahan",
                region = "Nasional",
                imageUrl = "https://images.unsplash.com/photo-1577962917302-cd874c4e31d2?auto=format&fit=crop&w=900&q=80",
                articleUrl = "https://www.samuderakepri.co.id",
                author = "Biro Nasional Samudera Kepri",
                publishedLabel = "3 Jam Lalu",
                publishedTimestamp = now - 3 * 3600_000L,
                readTimeMinutes = 3,
                isHero = false,
                isInvestigative = false,
                isExclusive = false
            ),
            ArticleEntity(
                id = "seed-ekonomi-sotk-maritim",
                title = "Dampak Transisi SOTK, Pelaku Usaha Maritim Kepri Dorong Percepatan Layanan Perizinan Pelabuhan",
                excerpt = "Pelaku usaha kepelabuhanan dan logistik antarpulau mendesak sinkronisasi tata kelola guna menjaga kelancaran arus investasi dan perdagangan laut.",
                content = """
                    TANJUNGPINANG, SAMUDERAKEPRI — Sejumlah asosiasi pelaku usaha sektor kemaritiman dan logistik laut di Kepulauan Riau mendorong percepatan penyesuaian Struktur Organisasi dan Tata Kerja (SOTK) yang berdampak langsung pada layanan perizinan operasional kapal.

                    Para pengusaha menyebut kepastian alur birokrasi sangat krusial mengingat wilayah Kepri memiliki karakteristik geografis 96 persen lautan yang bergantung pada kelancaran rantai pasok kapal niaga dan kapal penumpang rakyat.
                """.trimIndent(),
                category = "Ekonomi & Bisnis",
                region = "Tanjungpinang",
                imageUrl = "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=900&q=80",
                articleUrl = "https://www.samuderakepri.co.id",
                author = "Biro Tanjungpinang",
                publishedLabel = "4 Jam Lalu",
                publishedTimestamp = now - 4 * 3600_000L,
                readTimeMinutes = 4,
                isHero = false,
                isInvestigative = false,
                isExclusive = false
            ),
            ArticleEntity(
                id = "seed-hukum-kacabjari-kundur",
                title = "Kacabjari Kundur Pimpin Pemusnahan Barang Bukti Pidum & Pidsus yang Telah Berkekuatan Hukum Tetap",
                excerpt = "Kejaksaan Negeri Karimun Cabang Kundur memusnahkan barang bukti perkara tindak pidana umum dan khusus yang telah inkrah bersama unsur Forkopimda.",
                content = """
                    KARIMUN, SAMUDERAKEPRI — Kepala Cabang Kejaksaan Negeri (Kacabjari) Karimun di Tanjung Batu Kundur memimpin langsung kegiatan pemusnahan barang bukti perkara tindak pidana umum (Pidum) dan tindak pidana khusus (Pidsus) yang telah memperoleh kekuatan hukum tetap (inkrah).

                    Kegiatan tersebut dihadiri unsur Forkopimda setempat, aparat kepolisian, serta tokoh masyarakat sebagai bentuk komitmen keterbukaan penegakan hukum di wilayah kepulauan.
                """.trimIndent(),
                category = "Hukum & Kriminal",
                region = "Karimun",
                imageUrl = "https://images.unsplash.com/photo-1453733197781-704fa5988075?auto=format&fit=crop&w=900&q=80",
                articleUrl = "https://www.samuderakepri.co.id",
                author = "Koresponden Karimun",
                publishedLabel = "5 Jam Lalu",
                publishedTimestamp = now - 5 * 3600_000L,
                readTimeMinutes = 3,
                isHero = false,
                isInvestigative = true,
                isExclusive = false
            ),
            ArticleEntity(
                id = "seed-pendidikan-beasiswa-pesisir",
                title = "Perkuat SDM Kepulauan, Program Beasiswa Vokasi Maritim Kepri Buka Akses untuk Pelajar Pulau Terluar",
                excerpt = "Pemerintah daerah bersama lembaga pendidikan vokasi membuka kuota afirmasi bagi putra-putri nelayan di Anambas, Natuna, dan Lingga.",
                content = """
                    BATAM, SAMUDERAKEPRI — Upaya meningkatkan kualitas sumber daya manusia di kawasan kepulauan terus diperkuat melalui skema beasiswa pendidikan vokasi kelautan dan teknologi perkapalan bagi pelajar dari pulau-pulau terluar Kepulauan Riau.

                    Program ini diarahkan agar generasi muda pesisir memiliki kompetensi teknis yang siap diserap oleh industri galangan kapal, logistik maritim, dan sektor perikanan modern.
                """.trimIndent(),
                category = "Pendidikan",
                region = "Batam",
                imageUrl = "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?auto=format&fit=crop&w=900&q=80",
                articleUrl = "https://www.samuderakepri.co.id",
                author = "Redaksi Pendidikan Kepri",
                publishedLabel = "6 Jam Lalu",
                publishedTimestamp = now - 6 * 3600_000L,
                readTimeMinutes = 3,
                isHero = false,
                isInvestigative = false,
                isExclusive = false
            ),
            ArticleEntity(
                id = "seed-maritim-dkp-nelayan",
                title = "Hak Jawab DKP Kepri Terkait Penyaluran Sarpras Kelautan & Bantuan Armada Nelayan Pesisir",
                excerpt = "Dinas Kelautan dan Perikanan Provinsi Kepri menyampaikan klarifikasi teknis atas penyaluran bantuan sarana prasarana bagi kelompok nelayan.",
                content = """
                    BATAM, SAMUDERAKEPRI — Dinas Kelautan dan Perikanan (DKP) Provinsi Kepulauan Riau memberikan hak jawab resmi menyusul pemberitaan terkait evaluasi penyaluran sarana dan prasarana (sarpras) perikanan tangkap.

                    Melalui keterangan tertulisnya, DKP Kepri menegaskan bahwa seluruh unit alat tangkap dan perahu bantuan telah melewati tahapan verifikasi kelompok usaha bersama (KUB) nelayan di kabupaten/kota.
                """.trimIndent(),
                category = "Maritim",
                region = "Batam",
                imageUrl = "https://images.unsplash.com/photo-1544551763-46a013bb70d5?auto=format&fit=crop&w=900&q=80",
                articleUrl = "https://www.samuderakepri.co.id",
                author = "Redaksi Samudera Kepri",
                publishedLabel = "7 Jam Lalu",
                publishedTimestamp = now - 7 * 3600_000L,
                readTimeMinutes = 4,
                isHero = false,
                isInvestigative = false,
                isExclusive = false
            )
        )
    }
}
