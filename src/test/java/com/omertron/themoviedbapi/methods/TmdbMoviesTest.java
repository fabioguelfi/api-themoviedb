/*
 *      Copyright (c) 2004-2015 Stuart Boston
 *
 *      This file is part of TheMovieDB API.
 *
 *      TheMovieDB API is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      any later version.
 *
 *      TheMovieDB API is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with TheMovieDB API.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.omertron.themoviedbapi.methods;

import com.omertron.themoviedbapi.AbstractTests;
import static com.omertron.themoviedbapi.AbstractTests.getApiKey;
import static com.omertron.themoviedbapi.AbstractTests.getHttpTools;
import com.omertron.themoviedbapi.ArtworkResults;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TestID;
import com.omertron.themoviedbapi.enumeration.ArtworkType;
import com.omertron.themoviedbapi.model.StatusCode;
import com.omertron.themoviedbapi.model.artwork.Artwork;
import com.omertron.themoviedbapi.model.change.ChangeKeyItem;
import com.omertron.themoviedbapi.model.change.ChangeListItem;
import com.omertron.themoviedbapi.model.keyword.Keyword;
import com.omertron.themoviedbapi.model.list.UserList;
import com.omertron.themoviedbapi.model.media.MediaCreditCast;
import com.omertron.themoviedbapi.model.media.MediaCreditList;
import com.omertron.themoviedbapi.model.media.MediaState;
import com.omertron.themoviedbapi.model.media.AlternativeTitle;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.omertron.themoviedbapi.model.movie.ReleaseInfo;
import com.omertron.themoviedbapi.model.media.Translation;
import com.omertron.themoviedbapi.model.media.Video;
import com.omertron.themoviedbapi.model.review.Review;
import com.omertron.themoviedbapi.results.TmdbResultsList;
import com.omertron.themoviedbapi.tools.MethodBase;
import com.omertron.themoviedbapi.wrapper.WrapperChanges;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author stuart.boston
 */
public class TmdbMoviesTest extends AbstractTests {

    private static TmdbMovies instance;
    private static final List<TestID> FILM_IDS = new ArrayList<TestID>();

    public TmdbMoviesTest() {
    }

    @BeforeClass
    public static void setUpClass() throws MovieDbException {
        doConfiguration();
        instance = new TmdbMovies(getApiKey(), getHttpTools());

        FILM_IDS.add(new TestID("Blade Runner", "tt0083658", 78, "Harrison Ford"));
        FILM_IDS.add(new TestID("Jupiter Ascending", "tt1617661", 76757, "Mila Kunis"));
        FILM_IDS.add(new TestID("Lucy", "tt2872732", 240832, "Morgan Freeman"));

    }

    /**
     * Test of getMovieInfo method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetMovieInfo() throws MovieDbException {
        LOG.info("getMovieInfo");

        String language = LANGUAGE_DEFAULT;
        String[] appendToResponse = null;

        for (TestID test : FILM_IDS) {
            MovieInfo result = instance.getMovieInfo(test.getTmdb(), language, appendToResponse);
            assertEquals("Wrong IMDB ID", test.getImdb(), result.getImdbID());
            assertEquals("Wrong title", test.getName(), result.getTitle());

        }
    }

    /**
     * Test of getMovieInfoImdb method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetMovieInfoImdb() throws MovieDbException {
        LOG.info("getMovieInfoImdb");
        String language = LANGUAGE_DEFAULT;
        String[] appendToResponse = null;

        for (TestID test : FILM_IDS) {
            MovieInfo result = instance.getMovieInfoImdb(test.getImdb(), language, appendToResponse);
            assertEquals("Wrong TMDB ID", test.getTmdb(), result.getId());
            assertEquals("Wrong title", test.getName(), result.getTitle());
        }
    }

    /**
     * Test of getMovieAccountState method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetMovieAccountState() throws MovieDbException {
        LOG.info("getMovieAccountState");

        for (TestID test : FILM_IDS) {
            MediaState result = instance.getMovieAccountState(test.getTmdb(), getSessionId());
            assertNotNull("Null result", result);
            assertTrue("Invalid rating", result.getRated() > -2f);
        }
    }

    /**
     * Test of getMovieAlternativeTitles method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetMovieAlternativeTitles() throws MovieDbException {
        LOG.info("getMovieAlternativeTitles");

        String country = "";
        String[] appendToResponse = null;

        for (TestID test : FILM_IDS) {
            TmdbResultsList<AlternativeTitle> result = instance.getMovieAlternativeTitles(test.getTmdb(), country, appendToResponse);
            assertFalse("No alt titles", result.isEmpty());
        }
    }

    /**
     * Test of getMovieCredits method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetMovieCredits() throws MovieDbException {
        LOG.info("getMovieCredits");

        String[] appendToResponse = null;

        for (TestID test : FILM_IDS) {
            MediaCreditList result = instance.getMovieCredits(test.getTmdb(), appendToResponse);
            assertNotNull(result);
            assertFalse(result.getCast().isEmpty());

            boolean found = false;
            for (MediaCreditCast p : result.getCast()) {
                if (test.getOther().equals(p.getName())) {
                    found = true;
                    break;
                }
            }
            assertTrue(test.getOther() + " not found in cast!", found);

            assertFalse(result.getCrew().isEmpty());
            break;
        }
    }

    /**
     * Test of getMovieImages method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetMovieImages() throws MovieDbException {
        LOG.info("getMovieImages");

        String language = LANGUAGE_DEFAULT;
        String[] appendToResponse = null;

        ArtworkResults results = new ArtworkResults();

        for (TestID test : FILM_IDS) {
            TmdbResultsList<Artwork> result = instance.getMovieImages(test.getTmdb(), language, appendToResponse);
            assertFalse("No artwork", result.isEmpty());
            for (Artwork artwork : result.getResults()) {
                results.found(artwork.getArtworkType());
            }

            // We should only have posters & backdrops
            results.validateResults(ArtworkType.POSTER, ArtworkType.BACKDROP);
        }
    }

    /**
     * Test of getMovieKeywords method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetMovieKeywords() throws MovieDbException {
        LOG.info("getMovieKeywords");

        String[] appendToResponse = null;

        for (TestID test : FILM_IDS) {
            TmdbResultsList<Keyword> result = instance.getMovieKeywords(test.getTmdb(), appendToResponse);
            assertFalse("No keywords", result.isEmpty());
        }
    }

    /**
     * Test of getMovieReleaseInfo method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetMovieReleaseInfo() throws MovieDbException {
        LOG.info("getMovieReleaseInfo");

        String language = LANGUAGE_DEFAULT;
        String[] appendToResponse = null;

        for (TestID test : FILM_IDS) {
            TmdbResultsList<ReleaseInfo> result = instance.getMovieReleaseInfo(test.getTmdb(), language, appendToResponse);
            assertFalse("No release info", result.isEmpty());
        }
    }

    /**
     * Test of getMovieVideos method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetMovieVideos() throws MovieDbException {
        LOG.info("getMovieVideos");

        String language = LANGUAGE_DEFAULT;
        String[] appendToResponse = null;
        boolean found = false;

        for (TestID test : FILM_IDS) {
            TmdbResultsList<Video> result = instance.getMovieVideos(test.getTmdb(), language, appendToResponse);
            found = found || !result.isEmpty();
        }
        assertTrue("No videos", found);
    }

    /**
     * Test of getMovieTranslations method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetMovieTranslations() throws MovieDbException {
        LOG.info("getMovieTranslations");

        String[] appendToResponse = null;

        for (TestID test : FILM_IDS) {
            TmdbResultsList<Translation> result = instance.getMovieTranslations(test.getTmdb(), appendToResponse);
            assertFalse("No translations", result.isEmpty());
        }
    }

    /**
     * Test of getSimilarMovies method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetSimilarMovies() throws MovieDbException {
        LOG.info("getSimilarMovies");

        Integer page = null;
        String language = LANGUAGE_DEFAULT;
        String[] appendToResponse = null;

        for (TestID test : FILM_IDS) {
            TmdbResultsList<MovieInfo> result = instance.getSimilarMovies(test.getTmdb(), page, language, appendToResponse);
            assertFalse("No similar movies", result.isEmpty());
        }
    }

    /**
     * Test of getMovieReviews method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetMovieReviews() throws MovieDbException {
        LOG.info("getMovieReviews");

        Integer page = null;
        String language = LANGUAGE_DEFAULT;
        String[] appendToResponse = null;

        for (TestID test : FILM_IDS) {
            if (test.getTmdb() == 76757) {
                // Has no reviews
                continue;
            }
            TmdbResultsList<Review> result = instance.getMovieReviews(test.getTmdb(), page, language, appendToResponse);
            assertFalse("No reviews", result.isEmpty());
        }
    }

    /**
     * Test of getMovieLists method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetMovieLists() throws MovieDbException {
        LOG.info("getMovieLists");

        Integer page = null;
        String language = LANGUAGE_DEFAULT;
        String[] appendToResponse = null;

        for (TestID test : FILM_IDS) {
            TmdbResultsList<UserList> result = instance.getMovieLists(test.getTmdb(), page, language, appendToResponse);
            assertFalse("Empty list", result.isEmpty());
            assertTrue(result.getTotalResults() > 0);
        }
    }

    /**
     * Test of getMovieChanges method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetMovieChanges() throws MovieDbException {
        LOG.info("getMovieChanges");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = sdf.format(DateUtils.addDays(new Date(), -14));
        String endDate = "";
        int maxCheck = 5;

        TmdbChanges chgs = new TmdbChanges(getApiKey(), getHttpTools());
        List<ChangeListItem> changeList = chgs.getChangeList(MethodBase.MOVIE, null, null, null);
        LOG.info("Found {} changes to check, will check maximum of {}", changeList.size(), maxCheck);

        int count = 1;
        WrapperChanges result;
        for (ChangeListItem item : changeList) {
            result = instance.getMovieChanges(item.getId(), startDate, endDate);
            for (ChangeKeyItem ci : result.getChangedItems()) {
                assertNotNull("Null changes", ci);
            }

            if (count++ > maxCheck) {
                break;
            }
        }
    }

    /**
     * Test of postMovieRating method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testPostMovieRating() throws MovieDbException {
        LOG.info("postMovieRating");
        Integer rating = new Random().nextInt(10) + 1;

        for (TestID test : FILM_IDS) {
            StatusCode result = instance.postMovieRating(test.getTmdb(), rating, getSessionId(), null);
            assertEquals("failed to post rating", 12, result.getCode());
        }
    }

    /**
     * Test of getLatestMovie method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetLatestMovie() throws MovieDbException {
        LOG.info("getLatestMovie");

        MovieInfo result = instance.getLatestMovie();
        assertNotNull("Null movie returned", result);
        assertTrue("No ID", result.getId() > 0);
        assertTrue("No title", StringUtils.isNotBlank(result.getTitle()));
    }

    /**
     * Test of getUpcoming method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetUpcoming() throws MovieDbException {
        LOG.info("getUpcoming");
        Integer page = null;
        String language = LANGUAGE_DEFAULT;

        TmdbResultsList<MovieInfo> result = instance.getUpcoming(page, language);
        assertFalse("No results found", result.isEmpty());
    }

    /**
     * Test of getNowPlayingMovies method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetNowPlayingMovies() throws MovieDbException {
        LOG.info("getNowPlayingMovies");
        Integer page = null;
        String language = LANGUAGE_DEFAULT;

        TmdbResultsList<MovieInfo> result = instance.getNowPlayingMovies(page, language);
        assertFalse("No results found", result.isEmpty());
    }

    /**
     * Test of getPopularMovieList method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetPopularMovieList() throws MovieDbException {
        LOG.info("getPopularMovieList");
        Integer page = null;
        String language = LANGUAGE_DEFAULT;

        TmdbResultsList<MovieInfo> result = instance.getPopularMovieList(page, language);
        assertFalse("No results found", result.isEmpty());
    }

    /**
     * Test of getTopRatedMovies method, of class TmdbMovies.
     *
     * @throws com.omertron.themoviedbapi.MovieDbException
     */
    @Test
    public void testGetTopRatedMovies() throws MovieDbException {
        LOG.info("getTopRatedMovies");
        Integer page = null;
        String language = LANGUAGE_DEFAULT;

        TmdbResultsList<MovieInfo> result = instance.getTopRatedMovies(page, language);
        assertFalse("No results found", result.isEmpty());
    }

}