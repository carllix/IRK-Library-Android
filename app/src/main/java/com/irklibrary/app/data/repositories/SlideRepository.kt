package com.irklibrary.app.data.repositories

import com.irklibrary.app.data.models.MataKuliahModel
import com.irklibrary.app.data.models.SlideModel
import com.irklibrary.app.data.models.SlideWithMatkulModel

class SlidesRepository {

    fun getAllMataKuliah(): List<MataKuliahModel> {
        return listOf(
            MataKuliahModel(
                matkul = "IF1220 Matematika Diskrit",
                slides = listOf(
                    SlideModel("Pengantar Matematika Diskrit", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025-2/00-Pengantar-Matematika-Diskrit-2025.pdf"),
                    SlideModel("Logika", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/01-Logika-2024.pdf"),
                    SlideModel("Himpunan (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025-2/02-Himpunan(2025)-1.pdf"),
                    SlideModel("Himpunan (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025-2/03-Himpunan(2025)-2.pdf"),
                    SlideModel("Himpunan (Bagian 3)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025-2/04-Himpunan(2025)-3.pdf"),
                    SlideModel("Relasi dan fungsi (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/05-Relasi-dan-Fungsi-Bagian1-(2024).pdf"),
                    SlideModel("Relasi dan Fungsi (Bagian 2: Relasi inversi, komposisi relasi, relasi n-ary, fungsi)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/06-Relasi-dan-Fungsi-Bagian2-(2024).pdf"),
                    SlideModel("Relasi dan Fungsi (Bagian 3: fungsi khusus, relasi kesetaraan, relasi pengurutan parsial, dan klosur relasi)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/07-Relasi-dan-Fungsi-Bagian3-(2024).pdf"),
                    SlideModel("Induksi matematika (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/08-Induksi-matematik-bagian1-2024.pdf"),
                    SlideModel("Induksi matematika (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/09-Induksi-matematik-bagian2-2024.pdf"),
                    SlideModel("Aljabar Boolean (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/12-Aljabar-Boolean-(2024)-bagian1.pdf"),
                    SlideModel("Aljabar Boolean (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/13-Aljabar-Boolean-(2024)-bagian2.pdf"),
                    SlideModel("Aljabar Boolean (Bagian 3)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/14-Aljabar-Boolean-(2024)-bagian3.pdf"),
                    SlideModel("Deretan, rekursi, dan relasi rekurens (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/10-Deretan, rekursi-dan-relasi-rekurens-(Bagian1)-2024.pdf"),
                    SlideModel("Deretan, rekursi, dan relasi rekurens (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/11-Deretan, rekursi-dan-relasi-rekurens-(Bagian2)-2024.pdf"),
                    SlideModel("Teori Bilangan (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/15-Teori-Bilangan-Bagian1-2024.pdf"),
                    SlideModel("Teori Bilangan (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/16-Teori-Bilangan-Bagian2-2024.pdf"),
                    SlideModel("Teori Bilangan (Bagian 3)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/17-Teori-Bilangan-Bagian3-2024.pdf"),
                    SlideModel("Kombinatorika (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/18-Kombinatorika-Bagian1-2024.pdf"),
                    SlideModel("Kombinatorika (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/19-Kombinatorika-Bagian2-2024.pdf"),
                    SlideModel("Graf (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/20-Graf-Bagian1-2024.pdf"),
                    SlideModel("Graf (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/21-Graf-Bagian2-2024.pdf"),
                    SlideModel("Graf (Bagian 3)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/22-Graf-Bagian3-2024.pdf"),
                    SlideModel("Pohon (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/23-Pohon-Bag1-2024.pdf"),
                    SlideModel("Pohon (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/24-Pohon-Bag2-2024.pdf"),
                    SlideModel("Kompleksitas algoritma (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/25-Kompleksitas-Algoritma-Bagian1-2024.pdf"),
                    SlideModel("Kompleksitas algoritma (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Matdis/2024-2025/26-Kompleksitas-Algoritma-Bagian2-2024.pdf")
                )
            ),
            MataKuliahModel(
                matkul = "IF2123 Aljabar Linier dan Geometri",
                slides = listOf(
                    SlideModel("Pengantar Aljabar Linier dan Geometri (2024)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2024-2025/Algeo-00-Pengantar-Aljabar-Geometri-(2024).pdf"),
                    SlideModel("Review matriks", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-01-Review-Matriks-2023.pdf"),
                    SlideModel("Matriks eselon", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-02-Matriks-Eselon-2023.pdf"),
                    SlideModel("Sistem persamaan linier (Bagian 1: Metode eliminasi Gauss)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-03-Sistem-Persamaan-Linier-2023.pdf"),
                    SlideModel("Sistem persamaan linier (Bagian 2: Tiga kemungkinan solusi sistem persamaan linier", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-04-Tiga-Kemungkinan-Solusi-SPL-2023.pdf"),
                    SlideModel("Sistem persamaan linier (Bagian 3: Metode eliminasi Gauss-Jordan)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-05-Sistem-Persamaan-Linier-2-2023.pdf"),
                    SlideModel("Contoh-contoh pemodelan sistem persamaan linier dalam dunia nyata dan sains", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-06-Aplikasi-SPL-1-2023.pdf"),
                    SlideModel("Contoh aplikasi eliminasi Gauss di dalam metode numerik", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-07-Aplikasi-SPL-2-2023.pdf"),
                    SlideModel("Pengantar pemrograman dengan Bahasa Java", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2024-2025/Algeo-10-Pengantar-Pemrograman-dengan-Bahasa-Java-2024.pdf"),
                    SlideModel("Vektor di ruang Euclidean (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2024-2025/Algeo-11-Vektor-di-Ruang-Euclidean-Bag1-2024.pdf"),
                    SlideModel("Vektor di ruang Euclidean (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-12-Vektor-di-Ruang-Euclidean-Bag2-2023.pdf"),
                    SlideModel("Vektor di ruang Euclidean (Bagian 3)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-13-Vektor-di-Ruang-Euclidean-Bag3-2023.pdf"),
                    SlideModel("Aplikasi dot product pada information retrieval", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-14-Aplikasi-dot-product-pada-IR-2023.pdf"),
                    SlideModel("Ruang vektor umum (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-15-Ruang-vektor-umum-Bagian1-2023.pdf"),
                    SlideModel("Ruang vektor umum (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-16-Ruang-vektor-umum-Bagian2-2023.pdf"),
                    SlideModel("Ruang vektor umum (Bagian 3)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-17-Ruang-vektor-umum-Bagian3-2023.pdf"),
                    SlideModel("Ruang vektor umum (Bagian 4)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-18-Ruang-vektor-umum-Bagian4-2023.pdf"),
                    SlideModel("Nilai Eigen dan Vektor Eigen (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-19-Nilai-Eigen-dan-Vektor-Eigen-Bagian1-2023.pdf"),
                    SlideModel("Nilai Eigen dan Vektor Eigen (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-20-Nilai-Eigen-dan-Vektor-Eigen-Bagian2-2023.pdf"),
                    SlideModel("Singular Value Decomposition (SVD) (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-21-Singular-value-decomposition-Bagian1-2023.pdf"),
                    SlideModel("Singular Value Decomposition (SVD) (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-22-Singular-value-decomposition-Bagian2-2023.pdf"),
                    SlideModel("Dekomposisi LU", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-23-Dekomposisi-LU-2023.pdf"),
                    SlideModel("Dekomposisi QR", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2024-2025/Algeo-23b-Dekomposisi-QR-2024.pdf"),
                    SlideModel("Aljabar kompleks", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-24-Aljabar-Kompleks-2023.pdf"),
                    SlideModel("Aljabar quaternion (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-25-Aljabar-Quaternion-Bagian1-2023.pdf"),
                    SlideModel("Aljabar geometri (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-27-Aljabar-Geometri-Bagian1-2023.pdf"),
                    SlideModel("Aljabar geometri (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-28-Aljabar-Geometri-Bagian2-2023.pdf"),
                    SlideModel("Perkalian geometri (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-29-Perkalian-Geometri-Bagian1-2023.pdf"),
                    SlideModel("Perkalian geometri (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-30-Perkalian-Geometri-Bagian2-2023.pdf"),
                    SlideModel("Pembahasan soal aljabar geometri dan perkalian geometri", "https://informatika.stei.itb.ac.id/~rinaldi.munir/AljabarGeometri/2023-2024/Algeo-31-Pembahasan-Soal-Aljabar-dan-Perkalian-Geometri.pdf")
                )
            ),
            MataKuliahModel(
                matkul = "IF2211 Strategi Algoritma",
                slides = listOf(
                    SlideModel("Pengantar Strategi Algoritma", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/01-Pengantar-Strategi-Algoritma-(2025).pdf"),
                    SlideModel("Algoritma Brute Force (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/02-Algoritma-Brute-Force-(2025)-Bag1.pdf"),
                    SlideModel("Algoritma Brute Force (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/03-Algoritma-Brute-Force-(2025)-Bag2.pdf"),
                    SlideModel("Algoritma Greedy (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/04-Algoritma-Greedy-(2025)-Bag1.pdf"),
                    SlideModel("Algoritma Greedy (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/05-Algoritma-Greedy-(2025)-Bag2.pdf"),
                    SlideModel("Algoritma Greedy (Bagian 3)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/06-Algoritma-Greedy-(2025)-Bag3.pdf"),
                    SlideModel("Algoritma Divide and Conquer (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/07-Algoritma-Divide-and-Conquer-(2025)-Bagian1.pdf"),
                    SlideModel("Algoritma Divide and Conquer (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/08-Algoritma-Divide-and-Conquer-(2025)-Bagian2.pdf"),
                    SlideModel("Algoritma Divide and Conquer (Bagian 3)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/09-Algoritma-Divide-and-Conquer-(2025)-Bagian3.pdf"),
                    SlideModel("Algoritma Divide and Conquer (Bagian 4)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/10-Algoritma-Divide-and-Conquer-(2025)-Bagian4.pdf"),
                    SlideModel("Algoritma Decrease and Conquer (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/11-Algoritma-Decrease-and-Conquer-2025-Bagian1.pdf"),
                    SlideModel("Algoritma Decrease and Conquer (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/12-Algoritma-Decrease-and-Conquer-2025-Bagian2.pdf"),
                    SlideModel("Breadth First Search (BFS) dan Depth First Search (DFS) - Bagian 1", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/13-BFS-DFS-(2025)-Bagian1.pdf"),
                    SlideModel("Breadth First Search (BFS) dan Depth First Search (DFS) - Bagian 2", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/14-BFS-DFS-(2025)-Bagian2.pdf"),
                    SlideModel("Algoritma runut-balik (backtracking) (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/15-Algoritma-backtracking-(2025)-Bagian1.pdf"),
                    SlideModel("Algoritma runut-balik (backtracking) (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/16-Algoritma-backtracking-(2025)-Bagian2.pdf"),
                    SlideModel("Algoritma branch and bound (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/17-Algoritma-Branch-and-Bound-(2025)-Bagian1.pdf"),
                    SlideModel("Algoritma branch and bound (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/18-Algoritma-Branch-and-Bound-(2025)-Bagian2.pdf"),
                    SlideModel("Algoritma branch and bound (Bagian 3)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/19-Algoritma-Branch-and-Bound-(2025)-Bagian3.pdf"),
                    SlideModel("Algoritma branch and bound (Bagian 4)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/20-Algoritma-Branch-and-Bound-(2025)-Bagian4.pdf"),
                    SlideModel("Penentuan rute (Route/Path Planning) - Bagian 1", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/21-Route-Planning-(2025)-Bagian1.pdf"),
                    SlideModel("Penentuan rute (Route/Path Planning) - Bagian 2", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/22-Route-Planning-(2025)-Bagian2.pdf"),
                    SlideModel("Pencocokan string (string matching) dengan algoritma brute force, KMP, Boyer-Moore", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/23-Pencocokan-string-(2025).pdf"),
                    SlideModel("Pencocokan string dengan regular expression (regex)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/24-String-Matching-dengan-Regex-(2025).pdf"),
                    SlideModel("Program Dinamis (Dynamic Programming) - Bagian 1", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/25-Program-Dinamis-(2025)-Bagian1.pdf"),
                    SlideModel("Program Dinamis (Dynamic Programming) - Bagian 2", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/26-Program-Dinamis-(2025)-Bagian2.pdf"),
                    SlideModel("Teori P, NP, dan NP-Complete (Bagian 1)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2019-2020/Teori-P-NP-dan-NPC-(Bagian 1).pdf"),
                    SlideModel("Teori P, NP, dan NP-Complete (Bagian 2)", "https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2019-2020/Teori-P-NP-dan-NPC-(Bagian 2).pdf")
                )
            )
        )
    }

    fun getAllSlides(): List<SlideWithMatkulModel> {
        return getAllMataKuliah().flatMap { mataKuliah ->
            SlideWithMatkulModel.fromMataKuliahModel(mataKuliah)
        }
    }

    fun getMatkulNames(): List<String> {
        return getAllMataKuliah().map { it.matkul }
    }

    fun searchSlides(query: String, slides: List<SlideWithMatkulModel>): List<SlideWithMatkulModel> {
        if (query.isBlank()) return slides

        return slides.filter { slide ->
            kmpSearch(slide.judul.lowercase(), query.lowercase()) != -1
        }
    }

    private fun kmpSearch(text: String, pattern: String): Int {
        if (pattern.isEmpty()) return 0

        val lps = computeLPSArray(pattern)
        var textIndex = 0
        var patternIndex = 0

        while (textIndex < text.length) {
            if (pattern[patternIndex] == text[textIndex]) {
                textIndex++
                patternIndex++
            }

            if (patternIndex == pattern.length) {
                return textIndex - patternIndex
            } else if (textIndex < text.length && pattern[patternIndex] != text[textIndex]) {
                if (patternIndex != 0) {
                    patternIndex = lps[patternIndex - 1]
                } else {
                    textIndex++
                }
            }
        }

        return -1
    }

    private fun computeLPSArray(pattern: String): IntArray {
        val lps = IntArray(pattern.length)
        var length = 0
        var i = 1

        while (i < pattern.length) {
            if (pattern[i] == pattern[length]) {
                length++
                lps[i] = length
                i++
            } else {
                if (length != 0) {
                    length = lps[length - 1]
                } else {
                    lps[i] = 0
                    i++
                }
            }
        }

        return lps
    }
}